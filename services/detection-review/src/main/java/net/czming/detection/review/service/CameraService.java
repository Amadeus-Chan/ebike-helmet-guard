package net.czming.detection.review.service;

import io.github.linpeilie.Converter;
import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.detection.review.mapper.CameraMapper;
import net.czming.model.detection.review.dto.CameraAddDto;
import net.czming.model.detection.review.dto.CameraDto;
import net.czming.model.detection.review.entity.Camera;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CameraService {

    private final CameraMapper cameraMapper;

    private final Converter converter;

    public CameraService(CameraMapper cameraMapper, Converter converter) {
        this.cameraMapper = cameraMapper;
        this.converter = converter;
    }

    public List<CameraDto> getAllCameras() {
        return cameraMapper.selectAll().stream().map(
                camera -> converter.convert(camera, CameraDto.class)
        ).toList();
    }

    public void add(CameraAddDto cameraAddDto) {
        Camera camera = converter.convert(cameraAddDto, Camera.class);
        cameraMapper.insert(camera);
    }

    public void delete(Long id) {
        Camera camera = cameraMapper.selectByCameraId(id);
        if (camera == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "目标摄像头不存在");

        cameraMapper.deleteById(id);
    }

    public String getCameraLocation(Long cameraId) {
        Camera camera = cameraMapper.selectByCameraId(cameraId);
        if (camera == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "目标摄像头不存在");

        return camera.getLocation();
    }

    public String getSecretKey(Long cameraId) {
        Camera camera = cameraMapper.selectByCameraId(cameraId);
        if (camera == null)
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "目标摄像头不存在");

        return camera.getSecretKey();
}
}
