package net.czming.detection.review.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file-store")
public class PathProperties {
    private String detectedImageDir;
    private String detectedImageUrlPrefix;
}
