package com.example.cachecontrol;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {

    private final CacheInterceptor interceptor;

    public CacheWebConfig(CacheInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
    }
}
