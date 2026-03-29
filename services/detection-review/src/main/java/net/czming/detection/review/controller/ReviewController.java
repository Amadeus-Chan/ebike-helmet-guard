package net.czming.detection.review.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.czming.common.annotation.Loggable;
import net.czming.common.result.R;
import net.czming.detection.review.service.ReviewService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@Validated
@Loggable
@RequestMapping("/review")
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Loggable(includeParams = false)
    @PostMapping("/review")
    public R<Void> review(@RequestParam("cameraId") @NotNull Long cameraId,
                          @RequestParam("secretKey") @NotBlank String secretKey,
                          @RequestParam("detectedImage") @NotNull MultipartFile detectedImage,
                          @RequestParam("captureTime") @NotNull LocalDateTime captureTime) {
        reviewService.submitReviewTask(cameraId, secretKey, detectedImage, captureTime);
        return R.ok();
    }
}
