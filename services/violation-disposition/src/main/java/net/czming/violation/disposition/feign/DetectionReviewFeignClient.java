package net.czming.violation.disposition.feign;

import net.czming.common.result.R;
import net.czming.model.detection.review.dto.CameraAddDto;
import net.czming.model.detection.review.dto.CameraDto;
import net.czming.violation.disposition.feign.config.DetectionReviewFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "detection-review", configuration = DetectionReviewFeignConfig.class)
public interface DetectionReviewFeignClient {

    @GetMapping("/camera/get-all")
    public List<CameraDto> getAllCameras();

    @PostMapping("/camera/add")
    public R<Void> addCamera(@RequestBody CameraAddDto cameraAddDto);

    @PostMapping("/camera/delete")
    public R<Void> deleteCamera(@RequestParam("id") Long id);
}
