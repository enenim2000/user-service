package com.elara.userservice.auth;


import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtil {

    private static HttpServletRequest getRequest() {

        return (HttpServletRequest) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
    }

    public static void setAuthToken(AuthToken authToken) {
        getRequest().setAttribute("auth_token", authToken);
    }

    public static AuthToken getAuthToken() {
        return (AuthToken) getRequest().getAttribute("auth_token");
    }

    public static String getToken() {
        return getRequest().getHeader("Authorization").replace("Bearer ", "");
    }

    public static String getClientId() {
        return getRequest().getHeader("x-auth-client-id");
    }

}