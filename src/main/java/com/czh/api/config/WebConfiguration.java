package com.czh.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer{
    // @Override
    // public
    // void addResourceHandlers(ResourceHandlerRegistry registry) {
    //     registry.addResourceHandler("static/**").addResourceLocations("classpath:/static/");
	// }
}
