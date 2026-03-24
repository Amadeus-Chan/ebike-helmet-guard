package net.czming.violation.disposition.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.model.violation.disposition.context.UserContext;
import net.czming.model.violation.disposition.context.UserContextHolder;
import net.czming.model.violation.disposition.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        UserContext userContext = UserContextHolder.getUserContext();
        if (userContext == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        if (User.Status.DISABLED.name().equals(userContext.getStatus()))
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "您的账户已被禁用");

        return true;
    }
}
