package net.czming.detection.review.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Component
@ConfigurationProperties(prefix = "model-path")
public class ModelPathProperties {

    @NotBlank
    private String detectModel;

    @NotBlank
    private String ocrDetectionModel;

    @NotBlank
    private String ocrRecognizeModel;

    @NotBlank
    private String ocrTextlineModel;

}
