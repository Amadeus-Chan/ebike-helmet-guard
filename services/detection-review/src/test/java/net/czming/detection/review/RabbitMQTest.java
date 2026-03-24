package net.czming.detection.review;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RabbitMQTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void test() {
        String message = "Hello World!";
        String queueName = "simple.queue";

        rabbitTemplate.convertAndSend(queueName, message);

    }
}
