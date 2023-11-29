package com.elara.accountservice.auth;


import javax.servlet.http.HttpServletRequest;

import com.elara.accountservice.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class RequestUtil {

    private static HttpServletRequest getRequest() {

        return (HttpServletRequest) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
    }

    public static AuthToken getAuthToken() {
        return (AuthToken) getRequest().getAttribute("auth_token");
    }

    public static void setAuthToken(AuthToken authToken) {
        getRequest().setAttribute("auth_token", authToken);
    }

    public static User getUser() {
        return (User) getRequest().getAttribute("user");
    }

    public static void setUser(User user) {
        getRequest().setAttribute("user", user);
    }

    public static String getToken() {
        return getRequest().getHeader("Authorization").replace("Bearer ", "");
    }

    public static String getClientId() {
        return getRequest().getHeader("client-id");
    }

    public static String getClientIp() {
        String remoteIp;
        remoteIp = getRequest().getHeader("X-FORWARDED-FOR");
        if (remoteIp == null || "".equals(remoteIp)) {
            remoteIp = getRequest().getRemoteAddr();
        }
        return remoteIp;
    }

    public static String getMethodAndPathUri() {
        log.info("getRequest().getContextPath(): {}", getRequest().getContextPath());
        log.info("getRequest().getMethod(): {}",  getRequest().getMethod());
        log.info("getRequest().getRequestURI(): {}", getRequest().getRequestURI());
        log.info("getRequest().getServletPath(): {}", getRequest().getServletPath());
        log.info("getRequest().getRequestURL(): {}", getRequest().getRequestURL());
        return getRequest().getMethod() + getRequest().getRequestURI();
    }

}
