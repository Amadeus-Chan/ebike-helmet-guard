package net.czming.detection.review.config;

import net.czming.detection.review.interceptor.InternalAuthInterceptor;
import net.czming.detection.review.properties.PathProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final PathProperties pathProperties;

    private final InternalAuthInterceptor internalAuthInterceptor;

    public WebMvcConfig(PathProperties pathProperties, InternalAuthInterceptor internalAuthInterceptor) {
        this.pathProperties = pathProperties;
        this.internalAuthInterceptor = internalAuthInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + pathProperties.getDetectedImageDir() + File.separator);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(internalAuthInterceptor)
                .addPathPatterns("/camera/**");
    }
}
