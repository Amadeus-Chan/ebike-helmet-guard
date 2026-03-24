package net.czming.violation.disposition.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import net.czming.common.annotation.Loggable;
import net.czming.common.result.R;
import net.czming.model.detection.review.dto.CameraAddDto;
import net.czming.model.detection.review.dto.CameraDto;
import net.czming.model.violation.disposition.vo.CameraVo;
import net.czming.violation.disposition.service.CameraService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Loggable
@RestController
@RequestMapping("/camera")
public class CameraController {

    private final CameraService cameraService;

    public CameraController(CameraService cameraService) {
        this.cameraService = cameraService;
    }

    @GetMapping("/get-all")
    public R<List<CameraVo>> getAllCameras() {
        List<CameraVo> allCameras = cameraService.getAllCameras();
        return R.ok(allCameras);
    }

    @PostMapping("/add")
    public void addCamera(@RequestBody @Valid CameraAddDto cameraAddDto) {
        cameraService.addCamera(cameraAddDto);
    }

    @PostMapping("/delete")
    public void deleteCamera(@RequestParam("id")  Long id) {
        cameraService.deleteCamera(id);
    }
}
