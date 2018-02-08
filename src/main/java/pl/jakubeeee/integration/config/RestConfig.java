package pl.jakubeeee.integration.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import pl.jakubeeee.integration.interceptors.RequestLoggerInterceptor;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestConfig {

    private RequestLoggerInterceptor requestLoggerInterceptor;

    @Autowired

    public RestConfig(RequestLoggerInterceptor requestLoggerInterceptor) {
        this.requestLoggerInterceptor = requestLoggerInterceptor;
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(requestLoggerInterceptor);
        restTemplate.setInterceptors(interceptors);
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }

}
