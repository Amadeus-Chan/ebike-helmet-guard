package net.czming.detection.review.config;

import lombok.RequiredArgsConstructor;
import net.czming.detection.review.interceptor.InternalAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    private final InternalAuthInterceptor internalAuthInterceptor;



    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(internalAuthInterceptor)
                .addPathPatterns("/camera/**");
    }
}
