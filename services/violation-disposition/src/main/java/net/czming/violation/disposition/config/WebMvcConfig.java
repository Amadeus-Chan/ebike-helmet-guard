package net.czming.violation.disposition.config;

import net.czming.violation.disposition.interceptor.AdminInterceptor;
import net.czming.violation.disposition.interceptor.InternalAuthInterceptor;
import net.czming.violation.disposition.interceptor.LoginInterceptor;
import net.czming.violation.disposition.interceptor.RefreshLoginExpirationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RefreshLoginExpirationInterceptor refreshLoginExpirationInterceptor;

    private final LoginInterceptor loginInterceptor;

    private final AdminInterceptor adminInterceptor;

    private final InternalAuthInterceptor internalAuthInterceptor;


    public WebMvcConfig(RefreshLoginExpirationInterceptor refreshLoginExpirationInterceptor,
                        LoginInterceptor loginInterceptor,
                        AdminInterceptor adminInterceptor,
                        InternalAuthInterceptor internalAuthInterceptor
    ) {
        this.refreshLoginExpirationInterceptor = refreshLoginExpirationInterceptor;
        this.loginInterceptor = loginInterceptor;
        this.adminInterceptor = adminInterceptor;
        this.internalAuthInterceptor = internalAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(refreshLoginExpirationInterceptor)
                .order(0);

        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                        "/auth/register",
                        "/auth/login",
                        "/auth/code-login",
                        "/sms/**",
                        "/user/get-username",
                        "/user/change-password",
                        "/user/change-phone",
                        "/violation/add",
                        "/violation/add-batch")
                .order(1);

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/user/change-role")
                .addPathPatterns("/user/change-status")
                .addPathPatterns("/ebike/**")
                .addPathPatterns("/camera/**")
                .excludePathPatterns("/ebike/get")
                .order(2);

        registry.addInterceptor(internalAuthInterceptor)
                .addPathPatterns("/violation/add")
                .addPathPatterns("/violation/add-batch");


    }
}
