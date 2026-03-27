package net.czming.detection.review.service;

import ai.djl.modality.cv.Image;
import cn.smartjavaai.common.cv.SmartImageFactory;
import cn.smartjavaai.common.entity.DetectionInfo;
import cn.smartjavaai.common.entity.DetectionRectangle;
import cn.smartjavaai.objectdetection.model.DetectorModel;
import cn.smartjavaai.ocr.config.OcrRecOptions;
import cn.smartjavaai.ocr.entity.OcrInfo;
import cn.smartjavaai.ocr.model.common.recognize.OcrCommonRecModel;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import net.czming.common.annotation.Loggable;
import net.czming.common.exception.AccessDeniedException;
import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.common.util.constants.RabbitMQConstants;
import net.czming.common.util.constants.RedisConstants;
import net.czming.detection.review.feign.ViolationDispositionFeignClient;
import net.czming.detection.review.mq.message.ReviewTask;
import net.czming.detection.review.properties.PathProperties;
import net.czming.detection.review.service.DetectionAssociationService.RiderMatch;
import net.czming.model.detection.review.entity.ReviewRecord;
import net.czming.model.detection.review.entity.ViolationResult;
import net.czming.model.violation.disposition.dto.ViolationAddDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class ReviewService {

    private final DetectorModel detectorModel;

    private final OcrCommonRecModel ocrCommonRecModel;

    private final CameraService cameraService;

    private final ReviewRecordService reviewRecordService;

    private final ViolationResultService violationResultService;

    private final DetectionAssociationService detectionAssociationService;

    private final ViolationDispositionFeignClient violationDispositionFeignClient;

    private final StringRedisTemplate stringRedisTemplate;

    private final RabbitTemplate rabbitTemplate;

    private final PathProperties pathProperties;


    public ReviewService(final DetectorModel detectorModel,
                         final OcrCommonRecModel ocrCommonRecModel,
                         final CameraService cameraService,
                         final ReviewRecordService reviewRecordService,
                         final ViolationResultService violationResultService,
                         final DetectionAssociationService detectionAssociationService,
                         final ViolationDispositionFeignClient violationDispositionFeignClient,
                         final StringRedisTemplate stringRedisTemplate,
                         final RabbitTemplate rabbitTemplate,
                         final PathProperties pathProperties) {
        this.detectorModel = detectorModel;
        this.ocrCommonRecModel = ocrCommonRecModel;
        this.cameraService = cameraService;
        this.reviewRecordService = reviewRecordService;
        this.violationResultService = violationResultService;
        this.detectionAssociationService = detectionAssociationService;
        this.violationDispositionFeignClient = violationDispositionFeignClient;
        this.stringRedisTemplate = stringRedisTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.pathProperties = pathProperties;
    }

    public void submitReviewTask(Long cameraId, String secretKey, MultipartFile detectedImageFile, LocalDateTime captureTime){

        String redisKey = RedisConstants.CAMERA_SECRET_KEY + cameraId;
        String cachedSecretKey = stringRedisTemplate.opsForValue().get(redisKey);

        if (StringUtils.hasText(cachedSecretKey)) {
            if (!cachedSecretKey.equals(secretKey))
                throw new AccessDeniedException();
            stringRedisTemplate.expire(redisKey, RedisConstants.CAMERA_SECRET_TTL, TimeUnit.SECONDS);
        } else {
            String dbSecretKey = cameraService.getSecretKey(cameraId);
            if (!secretKey.equals(dbSecretKey)) {
                throw new AccessDeniedException();
            }
            stringRedisTemplate.opsForValue().set(redisKey, secretKey, RedisConstants.CAMERA_SECRET_TTL, TimeUnit.SECONDS);
        }

        String location = cameraService.getCameraLocation(cameraId);

        String imageFileName = UUID.randomUUID() + ".jpg";


        File dir = new File(pathProperties.getDetectedImageDir());
        if (!dir.exists() && !dir.mkdirs()) {
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "detectedImage目录创建失败");
        }

        String fullPath = pathProperties.getDetectedImageDir() + File.separator + imageFileName;
        File file = new File(fullPath);

        try {

            detectedImageFile.transferTo(file);

            String detectedImageUrl = pathProperties.getDetectedImageUrlPrefix() + "/" + imageFileName;
            ReviewTask reviewTask = ReviewTask.builder()
                    .cameraId(cameraId)
                    .detectedImageUrl(detectedImageUrl)
                    .captureTime(captureTime)
                    .location(location)
                    .build();

            rabbitTemplate.convertAndSend(RabbitMQConstants.REVIEW_TASK_EXCHANGE, RabbitMQConstants.REVIEW_TASK_ROUTING_KEY, reviewTask);
        } catch (Exception e) {
            if (file.exists() && !file.delete()) {
                log.error("文件删除失败路径：{}" ,fullPath);
            }
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "复核任务提交失败");
        }

    }


    @Loggable
    @Transactional
    public void review(ReviewTask reviewTask) {
        try {

            Image image = SmartImageFactory.getInstance().fromUrl(reviewTask.getDetectedImageUrl());

            ReviewRecord reviewRecord = ReviewRecord.builder()
                    .cameraId(reviewTask.getCameraId())
                    .detectedImage(reviewTask.getDetectedImageUrl())
                    .reviewTime(LocalDateTime.now())
                    .captureTime(reviewTask.getCaptureTime())
                    .violated(false)
                    .build();


            List<DetectionInfo> detectionInfoList = detectorModel.detect(image).getDetectionInfoList();

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

            reviewRecordService.addReviewRecord(reviewRecord);

            List<ViolationResult> violationResultList = new ArrayList<>();
            riderMatchList.forEach(riderMatch -> {
                boolean hasHelmet = !riderMatch.getHelmets().isEmpty();
                boolean hasLicensePlate = riderMatch.getLicensePlate() != null;
                if (!hasHelmet && hasLicensePlate) {
                    String recognizeLicensePlate = recognizeLicensePlate(image, riderMatch);
                    if (!StringUtils.hasText(recognizeLicensePlate))
                        return;

                    ViolationResult violationResult = ViolationResult.builder()
                            .recordId(reviewRecord.getId())
                            .evidenceImage(reviewTask.getDetectedImageUrl())
                            .location(reviewTask.getLocation())
                            .violationTime(reviewTask.getCaptureTime())
                            .licensePlate(recognizeLicensePlate)
                            .build();

                    violationResultList.add(violationResult);
                }
            });


            if (violationResultList.isEmpty()) {
                log.info("没有违规被检测到");
                return;
            }

            log.info("检测到违规:{}", violationResultList);


            reviewRecordService.updateReviewRecordViolate(reviewRecord.getId(), true);

            submitViolations(violationResultList);
            violationResultService.batchAdd(violationResultList);

        } catch (Exception e) {
            log.error("复核任务处理失败，reviewTask={}", reviewTask, e);
            throw new BusinessException(ErrorEnum.BIZ_FAILED, e.getMessage());
        }
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

        violationDispositionFeignClient.batchAddViolation(violationAddDtoList);
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
