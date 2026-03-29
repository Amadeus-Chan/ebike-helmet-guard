package net.czming.common.util.constants;

public class RabbitMQConstants {

    public static final String REVIEW_TASK_EXCHANGE = "review.task.direct";
    public static final String REVIEW_TASK_QUEUE = "review.task.queue";
    public static final String REVIEW_TASK_ROUTING_KEY = "review.task";

    public static final String REVIEW_TASK_DELAY_EXCHANGE = "review.task.delay.direct";
    public static final String REVIEW_TASK_DELAY_QUEUE = "review.task.delay.queue";
    public static final String REVIEW_TASK_DELAY_ROUTING_KEY = "review.task.delay";
    public static final int REVIEW_TASK_DELAY_MILLIS = 60_000;

    public static final String REVIEW_TASK_FINAL_DLX = "review.task.final.dlx";
    public static final String REVIEW_TASK_FINAL_DLQ = "review.task.final.dlq";
    public static final String REVIEW_TASK_FINAL_DLK = "review.task.final";

    public static final int REVIEW_TASK_MAX_RETRY_TIMES = 3;
}
