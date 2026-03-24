package net.czming.violation.disposition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "net.czming")
public class ViolationDispositionMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ViolationDispositionMainApplication.class, args);
    }
}
