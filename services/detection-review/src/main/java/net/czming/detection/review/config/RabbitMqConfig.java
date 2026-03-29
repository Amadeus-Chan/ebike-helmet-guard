package net.czming.detection.review.config;

import net.czming.common.util.constants.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange reviewTaskExchange() {
        return new DirectExchange(RabbitMQConstants.REVIEW_TASK_EXCHANGE);
    }

    @Bean
    public Queue reviewTaskQueue() {
        return QueueBuilder.durable(RabbitMQConstants.REVIEW_TASK_QUEUE)
                .deadLetterExchange(RabbitMQConstants.REVIEW_TASK_DELAY_EXCHANGE)
                .deadLetterRoutingKey(RabbitMQConstants.REVIEW_TASK_DELAY_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding reviewTaskBinding() {
        return BindingBuilder.bind(reviewTaskQueue())
                .to(reviewTaskExchange())
                .with(RabbitMQConstants.REVIEW_TASK_ROUTING_KEY);
    }

    @Bean
    public DirectExchange reviewTaskDelayExchange() {
        return new DirectExchange(RabbitMQConstants.REVIEW_TASK_DELAY_EXCHANGE);
    }

    @Bean
    public Queue reviewTaskDelayQueue() {
        return QueueBuilder.durable(RabbitMQConstants.REVIEW_TASK_DELAY_QUEUE)
                .ttl(RabbitMQConstants.REVIEW_TASK_DELAY_MILLIS)
                .deadLetterExchange(RabbitMQConstants.REVIEW_TASK_EXCHANGE)
                .deadLetterRoutingKey(RabbitMQConstants.REVIEW_TASK_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding reviewTaskDelayBinding() {
        return BindingBuilder.bind(reviewTaskDelayQueue())
                .to(reviewTaskDelayExchange())
                .with(RabbitMQConstants.REVIEW_TASK_DELAY_ROUTING_KEY);
    }

    @Bean
    public DirectExchange reviewTaskFinalDlx() {
        return new DirectExchange(RabbitMQConstants.REVIEW_TASK_FINAL_DLX);
    }
    @Bean
    public Queue reviewTaskFinalDlq() {
        return QueueBuilder.durable(RabbitMQConstants.REVIEW_TASK_FINAL_DLQ).build();
    }

    @Bean
    public Binding reviewTaskFinalDlqBinding() {
        return BindingBuilder.bind(reviewTaskFinalDlq())
                .to(reviewTaskFinalDlx())
                .with(RabbitMQConstants.REVIEW_TASK_FINAL_DLK);
    }


}
