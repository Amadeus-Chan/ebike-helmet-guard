package net.czming.violation.disposition.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.czming.model.violation.disposition.entity.User;
import net.czming.model.violation.disposition.context.UserContext;
import net.czming.model.violation.disposition.context.UserContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        UserContext userContext = UserContextHolder.getUserContext();
        String role = userContext.getRole();

        if (!role.equals(User.Role.ADMIN.name())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }
}
