package net.czming.violation.disposition.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "access-key")
public class AccessKeyProperties {
    private String id;
    private String secret;
}
