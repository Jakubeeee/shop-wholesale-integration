package com.jakubeeee.core.config;

import com.jakubeeee.core.interceptors.RequestLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.ArrayList;

import static java.util.Arrays.asList;

@Configuration
public class RestConfig {

    @Autowired
    RequestLoggingInterceptor requestLoggingInterceptor;

    @Value("${enable.request.logging.interceptor}")
    boolean IS_REQUEST_LOGGER_INTERCEPTOR_ENABLED;

    @Bean
    public RestTemplate restTemplate() {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        var restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));
        var interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        if (IS_REQUEST_LOGGER_INTERCEPTOR_ENABLED) interceptors.add(requestLoggingInterceptor);
        restTemplate.setInterceptors(interceptors);
        var jacksonConverter = new MappingJackson2HttpMessageConverter();
        jacksonConverter.setDefaultCharset(Charset.forName("UTF-8"));
        jacksonConverter.setSupportedMediaTypes(asList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));
        restTemplate.getMessageConverters().add(0, jacksonConverter);
        var stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringConverter.setSupportedMediaTypes(asList(MediaType.APPLICATION_XML, new MediaType("application", "force-download")));
        restTemplate.getMessageConverters().add(0, stringConverter);
        return restTemplate;
    }

}
