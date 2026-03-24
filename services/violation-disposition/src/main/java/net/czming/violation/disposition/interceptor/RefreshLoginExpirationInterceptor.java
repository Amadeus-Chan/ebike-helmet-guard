package net.czming.violation.disposition.interceptor;


import io.github.linpeilie.Converter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.czming.common.util.constants.RedisConstants;
import net.czming.model.violation.disposition.context.UserContext;
import net.czming.model.violation.disposition.context.UserContextHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class RefreshLoginExpirationInterceptor implements HandlerInterceptor {


    private final StringRedisTemplate stringRedisTemplate;

    private final Converter converter;


    public RefreshLoginExpirationInterceptor(StringRedisTemplate stringRedisTemplate, Converter converter) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.converter = converter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        String token = request.getHeader("Authorization");

        if (token == null || token.isBlank())
            return true;


        String loginKey = RedisConstants.LOGIN_USER_KEY + token;
        Map<Object, Object> userContextRawMap = stringRedisTemplate.opsForHash().entries(loginKey);

        if (userContextRawMap.isEmpty())
            return true;

        Map<String, Object> userContextMap = userContextRawMap.entrySet().stream().collect(Collectors.toMap(
                entry -> (String) entry.getKey(),
                Map.Entry::getValue
        ));


        UserContext userContext = converter.convert(userContextMap, UserContext.class);

        UserContextHolder.saveUserContext(userContext);

        String userTokenKey = RedisConstants.USER_TOKEN_KEY + userContext.getUsername();
        stringRedisTemplate.expire(loginKey, RedisConstants.TOKEN_TTL, TimeUnit.SECONDS);
        stringRedisTemplate.expire(userTokenKey, RedisConstants.TOKEN_TTL, TimeUnit.SECONDS);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContextHolder.removeUserContext();
    }
}
