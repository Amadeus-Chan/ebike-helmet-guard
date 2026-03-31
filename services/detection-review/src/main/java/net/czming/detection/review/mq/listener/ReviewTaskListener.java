package net.czming.detection.review.mq.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.czming.common.util.constants.RabbitMQConstants;
import net.czming.detection.review.service.ReviewService;
import net.czming.detection.review.service.ReviewTaskService;
import net.czming.model.detection.review.entity.ReviewTask;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewTaskListener {

    private final ReviewService reviewService;
    private final ReviewTaskService reviewTaskService;

    @RabbitListener(queues = RabbitMQConstants.REVIEW_TASK_QUEUE, concurrency = "2")
    public void listen(Long reviewTaskId) {


        ReviewTask reviewTask = reviewTaskService.getReviewTaskById(reviewTaskId);

        try {
            reviewService.review(new ReviewTask(reviewTask));
        } catch (Exception e) {

            reviewTaskService.increaseRetryCount(reviewTaskId);
            int retryCount = reviewTask.getRetryCount() + 1;
            if (retryCount < RabbitMQConstants.REVIEW_TASK_MAX_RETRY_TIMES) {
                log.error("复核失败，进入延迟队列。taskId={}, retryCount={}", reviewTaskId, retryCount, e);
                throw e;
            }

            reviewTaskService.markReviewTaskInvalid(reviewTaskId);
        }
    }
}
