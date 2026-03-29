package net.czming.detection.review.mq.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.czming.common.util.constants.RabbitMQConstants;
import net.czming.detection.review.service.ReviewService;
import net.czming.detection.review.service.ReviewTaskService;
import net.czming.model.detection.review.entity.ReviewTask;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewTaskListener {

    private final ReviewService reviewService;

    private final ReviewTaskService reviewTaskService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitMQConstants.REVIEW_TASK_QUEUE,
                    durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = RabbitMQConstants.REVIEW_TASK_DLX),
                            @Argument(name = "x-dead-letter-routing-key", value = RabbitMQConstants.REVIEW_TASK_DLK)
                    }),
            exchange = @Exchange(name = RabbitMQConstants.REVIEW_TASK_EXCHANGE),
            key = RabbitMQConstants.REVIEW_TASK_ROUTING_KEY
    ))
    public void listen(Long reviewTaskId) {
        reviewService.review(reviewTaskId);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitMQConstants.REVIEW_TASK_DLQ, durable = "true"),
            exchange = @Exchange(name = RabbitMQConstants.REVIEW_TASK_DLX),
            key = RabbitMQConstants.REVIEW_TASK_DLK
    ))
    public void deadLetterListen(Long reviewTaskId) {
        log.error("死信任务: {}", reviewTaskId);
        reviewTaskService.markReviewTaskInvalid(reviewTaskId);
    }
}
