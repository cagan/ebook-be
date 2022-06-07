package com.cagan.library.config;

import com.cagan.library.web.interceptor.AuthUserResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private AuthUserResolver authUserResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authUserResolver);
    }
}
