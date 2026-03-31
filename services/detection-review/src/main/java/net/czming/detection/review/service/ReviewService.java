package net.czming.detection.review.service;

import ai.djl.modality.cv.Image;
import cn.smartjavaai.common.cv.SmartImageFactory;
import cn.smartjavaai.common.entity.DetectionInfo;
import cn.smartjavaai.common.entity.DetectionRectangle;
import cn.smartjavaai.common.entity.DetectionResponse;
import cn.smartjavaai.objectdetection.model.DetectorModel;
import cn.smartjavaai.ocr.config.OcrRecOptions;
import cn.smartjavaai.ocr.entity.OcrInfo;
import cn.smartjavaai.ocr.model.common.recognize.OcrCommonRecModel;
import io.minio.GetObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.czming.common.annotation.Loggable;
import net.czming.common.exception.AccessDeniedException;
import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.common.result.R;
import net.czming.common.util.constants.RabbitMQConstants;
import net.czming.common.util.constants.RedisConstants;
import net.czming.detection.review.feign.ViolationDispositionFeignClient;
import net.czming.detection.review.properties.PathProperties;
import net.czming.detection.review.service.DetectionAssociationService.RiderMatch;
import net.czming.model.detection.review.entity.Camera;
import net.czming.model.detection.review.entity.ReviewTask;
import net.czming.model.detection.review.entity.ViolationResult;
import net.czming.model.violation.disposition.dto.ViolationAddDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Slf4j
@Service
public class ReviewService {

    private final DetectorModel detectorModel;

    private final OcrCommonRecModel ocrCommonRecModel;

    private final CameraService cameraService;

    private final ReviewTaskService reviewTaskService;

    private final ViolationResultService violationResultService;

    private final DetectionAssociationService detectionAssociationService;

    private final FileStorageService fileStorageService;

    private final ViolationDispositionFeignClient violationDispositionFeignClient;

    private final StringRedisTemplate stringRedisTemplate;

    private final RabbitTemplate rabbitTemplate;




    public void dispatchPendingTasks() {
        for (ReviewTask reviewTask : reviewTaskService.getPendingReviewTask()) {
            rabbitTemplate.convertAndSend(RabbitMQConstants.REVIEW_TASK_EXCHANGE, RabbitMQConstants.REVIEW_TASK_ROUTING_KEY, reviewTask.getId());
        }
    }


    public void submitReviewTask(Long cameraId, String secretKey, MultipartFile detectedImageFile, LocalDateTime captureTime) {

        Camera camera = validateCameraSecretKey(cameraId, secretKey);

        String detectedImageKey = fileStorageService.uploadDetectedImage(detectedImageFile, captureTime);
        ReviewTask reviewTask = ReviewTask.builder()
                .cameraId(cameraId)
                .detectedImage(detectedImageKey)
                .location(camera.getLocation())
                .captureTime(captureTime)
                .violated(false)
                .status(ReviewTask.Status.PENDING)
                .retryCount(0)
                .build();

        reviewTaskService.addReviewTask(reviewTask);

        if (reviewTask.getId() != null)
            rabbitTemplate.convertAndSend(RabbitMQConstants.REVIEW_TASK_EXCHANGE, RabbitMQConstants.REVIEW_TASK_ROUTING_KEY, reviewTask.getId());
    }


    @Loggable
    @Transactional
    public void review(ReviewTask reviewTask) {

        Long reviewTaskId = reviewTask.getId();

        if (reviewTaskService.markProcessingIfPending(reviewTaskId) != 1) {
            log.info("复核任务已被其他实例抢占或已处理，reviewTaskId={}", reviewTaskId);
            return;
        }

        Image image;
        try (GetObjectResponse getObjectResponse = fileStorageService.getObject(reviewTask.getDetectedImage())) {
            image = SmartImageFactory.getInstance().fromInputStream(getObjectResponse);
        } catch (Exception e) {
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "从GetObjectResponse获取Image失败");
        }

        DetectionResponse detectionResponse = detectorModel.detect(image);

        if (detectionResponse == null) {
            reviewTask.setReviewTime(LocalDateTime.now());
            reviewTask.setStatus(ReviewTask.Status.PROCESSED);
            reviewTaskService.updateReviewTask(reviewTask);
            log.info("没有违规被检测到，detectionResponse为null，reviewTaskId={}", reviewTask);
            return;
        }


        List<DetectionInfo> detectionInfoList = detectionResponse.getDetectionInfoList();

        if (detectionInfoList == null || detectionInfoList.isEmpty()) {
            reviewTask.setReviewTime(LocalDateTime.now());
            reviewTask.setStatus(ReviewTask.Status.PROCESSED);
            reviewTaskService.updateReviewTask(reviewTask);
            log.info("没有违规被检测到，detectionInfoList为空，reviewTaskId={}", reviewTask);
            return;
        }

        List<DetectionInfo> motorcyclistList = new ArrayList<>();
        List<DetectionInfo> helmetList = new ArrayList<>();
        List<DetectionInfo> licensePlateList = new ArrayList<>();


        detectionInfoList.forEach(detectionInfo -> {
            String type = detectionInfo.getObjectDetInfo().getClassName();
            switch (type) {
                case "motorcyclist" -> motorcyclistList.add(detectionInfo);
                case "helmet" -> helmetList.add(detectionInfo);
                case "license_plate" -> licensePlateList.add(detectionInfo);
            }
        });

        List<RiderMatch> riderMatchList =
                detectionAssociationService.associate(
                        motorcyclistList,
                        helmetList,
                        licensePlateList,
                        image.getWidth(),
                        image.getHeight()
                );


        List<ViolationResult> violationResultList = new ArrayList<>();

        for (RiderMatch riderMatch : riderMatchList) {

            boolean hasHelmet = !riderMatch.getHelmets().isEmpty();
            boolean hasLicensePlate = riderMatch.getLicensePlate() != null;

            if (!hasHelmet && hasLicensePlate) {
                String recognizeLicensePlate = recognizeLicensePlate(image, riderMatch);

                if (!StringUtils.hasText(recognizeLicensePlate))
                    continue;

                ViolationResult violationResult = ViolationResult.builder()
                        .recordId(reviewTaskId)
                        .evidenceImage(reviewTask.getDetectedImage())
                        .location(reviewTask.getLocation())
                        .violationTime(reviewTask.getCaptureTime())
                        .licensePlate(recognizeLicensePlate)
                        .build();

                violationResultList.add(violationResult);
            }
        }

        reviewTask.setReviewTime(LocalDateTime.now());
        reviewTask.setStatus(ReviewTask.Status.PROCESSED);

        if (violationResultList.isEmpty()) {
            log.info("没有违规被检测到，reviewTaskId={}", reviewTask);
            reviewTaskService.updateReviewTask(reviewTask);
            return;
        }

        log.info("检测到违规，reviewTaskId={}", reviewTask);

        reviewTask.setViolated(true);
        reviewTaskService.updateReviewTask(reviewTask);

        violationResultService.batchAdd(violationResultList);
        submitViolations(violationResultList);
    }

    private Camera validateCameraSecretKey(Long cameraId, String secretKey) {

        String redisKey = RedisConstants.CAMERA_SECRET_KEY + cameraId;
        String cachedSecretKey = stringRedisTemplate.opsForValue().get(redisKey);

        Camera camera = cameraService.getCameraById(cameraId);

        if (StringUtils.hasText(cachedSecretKey)) {
            if (!cachedSecretKey.equals(secretKey))
                throw new AccessDeniedException();
            stringRedisTemplate.expire(redisKey, RedisConstants.CAMERA_SECRET_TTL, TimeUnit.SECONDS);
        } else {
            String dbSecretKey = camera.getSecretKey();
            if (!secretKey.equals(dbSecretKey)) {
                throw new AccessDeniedException();
            }
            stringRedisTemplate.opsForValue().set(redisKey, secretKey, RedisConstants.CAMERA_SECRET_TTL, TimeUnit.SECONDS);
        }

        return camera;
    }

    private void submitViolations(List<ViolationResult> violationResultList) {
        List<ViolationAddDto> violationAddDtoList = violationResultList.stream().map(
                violationResult -> {
                    ViolationAddDto violationDto = new ViolationAddDto();
                    violationDto.setLicensePlate(violationResult.getLicensePlate());
                    violationDto.setLocation(violationResult.getLocation());
                    violationDto.setEvidenceImage(violationResult.getEvidenceImage());
                    violationDto.setTime(violationResult.getViolationTime());
                    return violationDto;
                }
        ).toList();

        R<Void> r = violationDispositionFeignClient.batchAddViolation(violationAddDtoList);
        if (r == null || r.getCode() != 200) {
            throw new BusinessException(ErrorEnum.INTERNAL_CALL_FAILED, "batchAddViolation失败");
        }
    }

    private String recognizeLicensePlate(Image image, RiderMatch riderMatch) {
        DetectionInfo licensePlateDetection = riderMatch.getLicensePlate();

        if (licensePlateDetection == null) {
            return null;
        }

        Image plateImage = cropLicensePlate(image, licensePlateDetection, 0.08, 0.15);

        OcrInfo ocrInfo = ocrCommonRecModel.recognize(
                plateImage,
                new OcrRecOptions(true, false)
        );

        return normalizeLicensePlate(ocrInfo.getFullText());
    }

    private Image cropLicensePlate(
            Image image,
            DetectionInfo licensePlateDetection,
            double expandRatioX,
            double expandRatioY
    ) {
        DetectionRectangle rect = licensePlateDetection.getDetectionRectangle();

        int x = rect.getX();
        int y = rect.getY();
        int width = rect.getWidth();
        int height = rect.getHeight();

        int expandX = (int) Math.round(width * expandRatioX);
        int expandY = (int) Math.round(height * expandRatioY);

        int left = Math.max(0, x - expandX);
        int top = Math.max(0, y - expandY);
        int right = Math.min(image.getWidth(), x + width + expandX);
        int bottom = Math.min(image.getHeight(), y + height + expandY);

        int cropWidth = right - left;
        int cropHeight = bottom - top;

        if (cropWidth <= 0 || cropHeight <= 0) {
            throw new IllegalArgumentException("车牌裁剪区域无效");
        }

        return image.getSubImage(left, top, cropWidth, cropHeight);
    }

    private String normalizeLicensePlate(String rawText) {
        if (rawText == null) {
            return null;
        }

        String normalized = rawText
                .replaceAll("\\s+", "")
                .replaceAll("[^\\u4e00-\\u9fa5A-Z0-9]", "")
                .trim()
                .toUpperCase();

        return normalized.isEmpty() ? null : normalized;
    }


}
