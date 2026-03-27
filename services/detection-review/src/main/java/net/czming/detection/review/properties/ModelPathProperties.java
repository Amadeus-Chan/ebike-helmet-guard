package net.czming.detection.review.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "model-path")
public class ModelPathProperties {

    private String detectModel;
    private String ocrDetectionModel;
    private String ocrRecognizeModel;
    private String ocrTextlineModel;

}
