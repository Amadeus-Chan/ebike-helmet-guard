package net.czming.detection.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "net.czming")
public class DetectionReviewMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(DetectionReviewMainApplication.class, args);
    }

}

