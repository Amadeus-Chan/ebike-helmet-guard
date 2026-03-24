package net.czming.detection.review.controller;

import jakarta.validation.Valid;
import net.czming.common.annotation.Loggable;
import net.czming.detection.review.service.CameraService;
import net.czming.model.detection.review.dto.CameraAddDto;
import net.czming.model.detection.review.dto.CameraDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Loggable
@RequestMapping("/camera")
@RestController
public class CameraController {

    private final CameraService cameraService;

    public CameraController(CameraService cameraService) {
        this.cameraService = cameraService;
    }

    @GetMapping("/get-all")
    public List<CameraDto> getAllCameras() {
        return cameraService.getAllCameras();
    }

    @PostMapping("/add")
    public void addCamera(@RequestBody @Valid CameraAddDto cameraAddDto) {
        cameraService.add(cameraAddDto);
    }

    @PostMapping("/delete")
    public void deleteCamera(@RequestParam("id")  Long id) {
        cameraService.delete(id);
    }
}
