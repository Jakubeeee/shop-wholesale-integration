package com.jakubeeee.core.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;

import static org.apache.cxf.common.util.Base64Utility.*;

/**
 * Utility class providing useful static methods related to rest communication.
 */
@UtilityClass
public final class RestUtils {

    public static HttpHeaders generateHeaderWithAuthToken(String tokenValue) {
        var headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + tokenValue);
        return headers;
    }

    public static HttpHeaders generateHeaderWithUsernameAndPassword(String username, String password) {
        var headers = new HttpHeaders();
        String userAndPass = username + ":" + password;
        headers.add("Authorization", "Basic " + encode(userAndPass.getBytes()));
        return headers;
    }

}
