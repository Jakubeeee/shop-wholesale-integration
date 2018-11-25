package com.jakubeeee.core.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * If activated, this interceptor watches every http request and response and logs information about them
 */
@Slf4j
@Component
public class RequestLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            infoRequest(request, body);
        } catch (UnknownHostException ignored) {
        }
        ClientHttpResponse response = execution.execute(request, body);
        try {
            infoResponse(response);
        } catch (UnknownHostException ignored) {
        }
        return response;
    }

    private void infoRequest(HttpRequest request, byte[] body) throws IOException {
        LOG.info("===========================request begin================================================");
        LOG.info("URI         : {}", request.getURI());
        LOG.info("Method      : {}", request.getMethod());
        LOG.info("Headers     : {}", request.getHeaders());
        LOG.info("Request body: {}", new String(body, UTF_8));
        LOG.info("==========================request end================================================");
    }

    private void infoResponse(ClientHttpResponse response) throws IOException {
        var inputStringBuilder = new StringBuilder();
        var bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), UTF_8));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }
        LOG.info("============================response begin==========================================");
        LOG.info("Status code  : {}", response.getStatusCode());
        LOG.info("Status text  : {}", response.getStatusText());
        LOG.info("Headers      : {}", response.getHeaders());
        LOG.info("Response body: {}", inputStringBuilder.toString());
        LOG.info("=======================response end=================================================");
    }

}