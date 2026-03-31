package net.czming.detection.review.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Component
@ConfigurationProperties(prefix = "file-store")
public class PathProperties {
    @NotBlank
    private String detectedImageDir;

    @NotBlank
    private String detectedImageUrlPrefix;
}
