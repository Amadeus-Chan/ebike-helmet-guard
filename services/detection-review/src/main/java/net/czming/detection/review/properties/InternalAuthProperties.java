package net.czming.detection.review.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "internal-auth")
public class InternalAuthProperties {
    private String serviceName;
    private String secretKey;
    private Map<String, String> trustedServiceSecrets;
}
