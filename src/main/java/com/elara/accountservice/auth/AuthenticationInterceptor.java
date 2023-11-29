package com.elara.accountservice.auth;

import com.elara.accountservice.dto.request.TokenVerifyRequest;
import com.elara.accountservice.dto.response.TokenVerifyResponse;
import com.elara.accountservice.enums.ResponseCode;
import com.elara.accountservice.exception.UnAuthorizedException;
import com.elara.accountservice.service.AuthenticationService;
import com.elara.accountservice.service.MessageService;
import com.elara.accountservice.util.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    /**
     * Block and Unblock user or customer, update pin when clicked by admin reset to default 0000, delete pin
     * Block and unblock loans
     * shutdown entire API centrally for all client or specific clients
     * dashboard report summary
     * Payment history
     *
     * Loan
     * Different type of loans
     * select loan to apply
     * Check the credit history, and other eligibility conditions
     * upload guarantor, ID, passport, etc
     * allow to apply for loan
     * conditional approval
     * Customer must accept
     * Each approval level on the loan activity has comments for each person that approve
     * When disburse is click we send the request to bankone to disburse to customer account
     * Send loan for approval request, by several people, if condition greater than 200,000 force certain level like MD approval included
     * Pick up loan and approval management system
     * Loan dashboard summary, no of approval volume approval amount,
     */
    @Value("${oauth.server.url}")
    private String authServerUrl;

    @Value("${oauth.server.client-id}")
    private String authServerClientId;

    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    MessageService messageService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("AuthenticationInterceptor::preHandle()");

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        if (!isSecuredRoute(handlerMethod)) {
            return true;
        }

        //Call Authorization server with token forward by ApiGateway, then add the client id, and service as header
        //Authorization server will authenticate the token that it generated and return response back
        //Check if response is authenticated and has permission, allow to proceed

        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String httpMethod = request.getMethod();
        String pathUri = request.getRequestURI();

        //Hash SHA 256 of appName,http method,uri e.g user-service,GET,/api/user/logout
        String permissionId = HashUtil.getHash(serviceName + httpMethod + pathUri);

       TokenVerifyResponse result = authenticationService.verifyToken(TokenVerifyRequest.builder()
                       .token(token)
                       .serviceClientId(authServerClientId)
                       .permissionId(permissionId)
                       .build());

       if (!ResponseCode.SUCCESSFUL.getValue().equals(result.getResponseCode())) {
           throw new UnAuthorizedException(messageService.getMessage("Token.Invalid"));
       }

        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("AuthenticationInterceptor::postHandle()");
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("AuthenticationInterceptor::afterCompletion()");
    }

    private boolean isSecuredRoute(HandlerMethod handlerMethod) {
        return handlerMethod.getMethod().isAnnotationPresent(
            Permission.class);
    }

   /* private boolean isPermitted(String forwardedToken, String httpMethod, String pathUri) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();

        //Hash SHA 256 of appName,http method,uri e.g user-service,GET,/api/user/logout
        String permissionId = HashUtil.getHash(serviceName + httpMethod + pathUri);

        header.add("x-auth-client-id", authServerClientId); //Service/App client id
        header.add("x-auth-client-token", forwardedToken); //Token forwarded by API Gateway or frontend client
        header.add("x-auth-permission-id", permissionId);

        HttpEntity<String> httpEntity = new HttpEntity<>(null, header);
        String oauthUrl = authServerUrl + "/oauth/token/verify";
        try {
            //The endpoint on the authorization server will check to see if the service has the permissionId in the ApplicationPermission table
            ResponseEntity<String> response = restTemplate.exchange(oauthUrl, HttpMethod.GET, httpEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return true;
            }
        } catch (Exception e) {
            log.error("Unable to verify token server url: {}", oauthUrl);
        }

        return false;
    }*/
}