package net.czming.violation.disposition.feign.config;

import feign.RequestInterceptor;
import net.czming.common.util.constants.HeaderConstants;
import net.czming.violation.disposition.properties.InternalAuthProperties;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.context.annotation.Bean;

public class DetectionReviewFeignConfig {
    private final InternalAuthProperties internalAuthProperties;

    public DetectionReviewFeignConfig(final InternalAuthProperties internalAuthProperties) {
        this.internalAuthProperties = internalAuthProperties;
    }

    @Bean
    public RequestInterceptor feignAuthInterceptor() {
        return requestTemplate -> {
            String serviceName = internalAuthProperties.getServiceName();
            String secretKey = internalAuthProperties.getSecretKey();

            String timestamp = Long.toString(System.currentTimeMillis());

            String contentToSign = "serviceName=" + serviceName + ";" + "timestamp=" + timestamp;
            String signature = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secretKey)
                    .hmacHex(contentToSign);

            requestTemplate.header(HeaderConstants.SERVICE_NAME, serviceName);
            requestTemplate.header(HeaderConstants.TIMESTAMP, timestamp);
            requestTemplate.header(HeaderConstants.SIGNATURE, signature);
        };
    }
}
