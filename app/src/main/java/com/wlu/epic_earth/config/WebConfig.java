package com.wlu.epic_earth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/data/images/**")
                .addResourceLocations("file:./data/images/");
        registry.addResourceHandler("/data/gifs/**")
                .addResourceLocations("file:./data/gifs/");
    }
}