package com.d2y.d2yapiofficial.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry cors) {
    cors
        .addMapping("/**")
        .allowedOriginPatterns("*")
        .allowedMethods("*")
        .allowedHeaders("*")
        .allowCredentials(true);
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(new MediaType("application", "vnd.api+json"), MediaType.APPLICATION_JSON);
  }

}
