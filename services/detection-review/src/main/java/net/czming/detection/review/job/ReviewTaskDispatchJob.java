package net.czming.detection.review.job;

import lombok.RequiredArgsConstructor;
import net.czming.common.annotation.Loggable;
import net.czming.detection.review.service.ReviewService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Loggable
@Component
@RequiredArgsConstructor
public class ReviewTaskDispatchJob {
    private final ReviewService reviewService;

    @Scheduled(fixedDelay =  12 * 60 * 60 * 60 * 1000L)
    public void dispatchPendingTasks() {
        reviewService.dispatchPendingTasks();
    }
}
