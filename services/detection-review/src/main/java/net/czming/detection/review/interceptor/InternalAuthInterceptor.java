package net.czming.detection.review.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.czming.common.util.constants.HeaderConstants;
import net.czming.detection.review.properties.InternalAuthProperties;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class InternalAuthInterceptor implements HandlerInterceptor {


    private static final long ALLOWED_TIME_SKEW_MILLIS = 5 * 60 * 1000L;

    private final InternalAuthProperties internalAuthProperties;

    public InternalAuthInterceptor(final InternalAuthProperties internalAuthProperties) {
        this.internalAuthProperties = internalAuthProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String serviceName = request.getHeader(HeaderConstants.SERVICE_NAME);
        String timestamp = request.getHeader(HeaderConstants.TIMESTAMP);
        String signature = request.getHeader(HeaderConstants.SIGNATURE);

        if (!StringUtils.hasText(serviceName) || !StringUtils.hasText(timestamp) || !StringUtils.hasText(signature)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }


        try {
            if (Math.abs(System.currentTimeMillis() - Long.parseLong(timestamp)) > ALLOWED_TIME_SKEW_MILLIS){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String secretKey = internalAuthProperties.getTrustedServiceSecrets().get(serviceName);
        if (!StringUtils.hasText(secretKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String contentToSign = "serviceName=" + serviceName + ";" + "timestamp=" + timestamp;
        String expectedSignature = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secretKey)
                .hmacHex(contentToSign);

        boolean matched = MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                signature.getBytes(StandardCharsets.UTF_8)
        );

        if (!matched) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        return true;
    }
}
