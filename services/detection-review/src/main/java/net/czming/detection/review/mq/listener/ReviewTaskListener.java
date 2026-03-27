package net.czming.detection.review.mq.listener;

import lombok.extern.slf4j.Slf4j;
import net.czming.common.util.constants.RabbitMQConstants;
import net.czming.detection.review.mq.message.ReviewTask;
import net.czming.detection.review.service.ReviewService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReviewTaskListener {

    private final ReviewService reviewService;

    public ReviewTaskListener(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitMQConstants.REVIEW_TASK_QUEUE, durable = "true"),
            exchange = @Exchange(name = RabbitMQConstants.REVIEW_TASK_EXCHANGE),
            key = RabbitMQConstants.REVIEW_TASK_ROUTING_KEY
    ))
    public void listen(ReviewTask reviewTask) {
        reviewService.review(reviewTask);
    }
}
