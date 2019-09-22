package com.jakubeeee.core.impl.config;

import com.jakubeeee.core.impl.debug.RequestLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static java.util.Arrays.asList;

/**
 * Java spring configuration file for application's rest communication.
 */
@Configuration
public class RestConfig {

    @Value("${enable.request.logging.interceptor}")
    boolean REQUEST_LOGGER_INTERCEPTOR_ENABLED;

    private final RequestLoggingInterceptor requestLoggingInterceptor;

    public RestConfig(RequestLoggingInterceptor requestLoggingInterceptor) {
        this.requestLoggingInterceptor = requestLoggingInterceptor;
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        var restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));
        var interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        if (REQUEST_LOGGER_INTERCEPTOR_ENABLED) interceptors.add(requestLoggingInterceptor);
        restTemplate.setInterceptors(interceptors);
        var jacksonConverter = new MappingJackson2HttpMessageConverter();
        jacksonConverter.setDefaultCharset(StandardCharsets.UTF_8);
        jacksonConverter.setSupportedMediaTypes(asList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));
        restTemplate.getMessageConverters().add(0, jacksonConverter);
        var stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringConverter.setSupportedMediaTypes(asList(MediaType.APPLICATION_XML, new MediaType("application", "force-download")));
        restTemplate.getMessageConverters().add(0, stringConverter);
        var builder = new RestTemplateBuilder();
        builder.configure(restTemplate);
        return builder;
    }

}
