package com.jakubeeee.core.service;

import com.jakubeeee.testutils.model.TestSubject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static com.jakubeeee.testutils.utils.TestSubjectUtils.getTestSubject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(RestService.class)
public class RestServiceTest {

    private static String GET_STRING_RESPONSE;
    private static TestSubject GET_JSON_OBJECT_RESPONSE;
    private static TestSubject POST_JSON_OBJECT_RESPONSE;
    private static TestSubject PUT_JSON_OBJECT_RESPONSE;
    private static HttpEntity<?> TEST_HTTP_ENTITY;
    
    @BeforeClass
    public static void setUp() {
        GET_STRING_RESPONSE = "test";
        GET_JSON_OBJECT_RESPONSE = getTestSubject(1);
        POST_JSON_OBJECT_RESPONSE = getTestSubject(2);
        PUT_JSON_OBJECT_RESPONSE = getTestSubject(3);
        TEST_HTTP_ENTITY = new HttpEntity<>(new HttpHeaders());
    }

    @Autowired
    private RestService restService;

    @Autowired
    private MockRestServiceServer server;

    @Test
    public void getStringTest() {
        String testUri = "/getString";
        configureRestServer(testUri, GET, GET_STRING_RESPONSE, TEXT_PLAIN);
        var result = restService.getString(testUri);
        assertThat(result, is(equalTo(GET_STRING_RESPONSE)));
    }

    @Test
    public void getJsonObjectTest() {
        String testUri = "/getJsonObject";
        configureRestServer(testUri, GET, GET_JSON_OBJECT_RESPONSE.asJson(), APPLICATION_JSON);
        var result = restService.getJsonObject(testUri, TEST_HTTP_ENTITY, TestSubject.class);
        assertHttpResponseStatusOK(result);
        assertThat(result.getBody(), is(equalTo((GET_JSON_OBJECT_RESPONSE))));
        assertContentTypeApplicationJson(result);
    }

    @Test
    public void postJsonObjectTest() {
        String testUri = "/postJsonObject";
        configureRestServer(testUri, POST, POST_JSON_OBJECT_RESPONSE.asJson(), APPLICATION_JSON);
        var result = restService.postJsonObject(testUri, TEST_HTTP_ENTITY, TestSubject.class);
        assertHttpResponseStatusOK(result);
        assertThat(result.getBody(), is(equalTo((POST_JSON_OBJECT_RESPONSE))));
        assertContentTypeApplicationJson(result);
    }

    @Test
    public void putJsonObjectTest() {
        String testUri = "/putJsonObject";
        configureRestServer(testUri, PUT, PUT_JSON_OBJECT_RESPONSE.asJson(), APPLICATION_JSON);
        var result = restService.putJsonObject(testUri, TEST_HTTP_ENTITY, TestSubject.class);
        assertHttpResponseStatusOK(result);
        assertThat(result.getBody(), is(equalTo((PUT_JSON_OBJECT_RESPONSE))));
        assertContentTypeApplicationJson(result);
    }

    private void configureRestServer(String uri, HttpMethod method, String response, MediaType mediaType) {
        server
                .expect(requestTo(uri))
                .andExpect(method(method))
                .andRespond(withSuccess(response, mediaType));
    }

    private void assertHttpResponseStatusOK(ResponseEntity<?> responseEntity) {
        assertThat(responseEntity.getStatusCode(), is(equalTo(OK)));
    }

    private void assertContentTypeApplicationJson(ResponseEntity<?> responseEntity) {
        assertThat(responseEntity.getHeaders().getContentType(), is(equalTo(APPLICATION_JSON)));
    }

}
