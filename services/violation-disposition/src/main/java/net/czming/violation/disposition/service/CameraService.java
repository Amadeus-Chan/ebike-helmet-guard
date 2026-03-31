package net.czming.violation.disposition.service;

import io.github.linpeilie.Converter;
import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.common.result.R;
import net.czming.model.detection.review.dto.CameraAddDto;
import net.czming.model.violation.disposition.vo.CameraVo;
import net.czming.violation.disposition.feign.DetectionReviewFeignClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CameraService {

    private final PermissionService permissionService;

    private final DetectionReviewFeignClient detectionReviewClient;

    private final Converter converter;

    public CameraService(PermissionService permissionService, final DetectionReviewFeignClient detectionReviewClient, final Converter converter) {
        this.permissionService = permissionService;
        this.detectionReviewClient = detectionReviewClient;
        this.converter = converter;
    }

    public List<CameraVo> getAllCameras() {
        permissionService.requireAdmin();

        return detectionReviewClient.getAllCameras().stream().map(
                cameraDto -> converter.convert(cameraDto, CameraVo.class)
        ).toList();
    }

    public void addCamera(CameraAddDto cameraAddDto) {
        permissionService.requireAdmin();

        R<Void> r = detectionReviewClient.addCamera(cameraAddDto);
        if (r.getCode() == 500)
            throw new BusinessException(ErrorEnum.INTERNAL_CALL_FAILED, "微服务调用失败：" + r.getData());
    }

    public void deleteCamera(Long cameraId) {
        permissionService.requireAdmin();

        R<Void> r = detectionReviewClient.deleteCamera(cameraId);
        if (r.getCode() == 500)
            throw new BusinessException(ErrorEnum.INTERNAL_CALL_FAILED, "微服务调用失败:" + r.getMessage());
    }
}
