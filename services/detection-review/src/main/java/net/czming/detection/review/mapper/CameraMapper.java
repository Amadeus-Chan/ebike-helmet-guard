package net.czming.detection.review.mapper;

import net.czming.model.detection.review.entity.Camera;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CameraMapper {

    Camera selectByCameraId(Long id);

    List<Camera> selectAll();

    int insert(Camera camera);

    int deleteById(Long id);
}
