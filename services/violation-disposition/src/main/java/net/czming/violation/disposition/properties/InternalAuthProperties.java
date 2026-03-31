package net.czming.violation.disposition.properties;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Validated
@Data
@Component
@ConfigurationProperties(prefix = "internal-auth")
public class InternalAuthProperties {
    @NotBlank
    private String serviceName;

    @NotBlank
    private String secretKey;

    @NotEmpty
    private Map<@NotBlank String, @NotBlank String> trustedServiceSecrets;
}
