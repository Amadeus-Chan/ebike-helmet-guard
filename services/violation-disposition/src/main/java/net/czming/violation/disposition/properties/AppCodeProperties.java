package net.czming.violation.disposition.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app-code")
public class AppCodeProperties {
    String threeCheck;
}
