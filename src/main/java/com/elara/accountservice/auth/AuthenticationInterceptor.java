package com.elara.accountservice.auth;

import com.elara.accountservice.domain.Company;
import com.elara.accountservice.dto.request.TokenVerifyRequest;
import com.elara.accountservice.dto.response.TokenVerifyResponse;
import com.elara.accountservice.enums.EntityStatus;
import com.elara.accountservice.enums.ResponseCode;
import com.elara.accountservice.exception.AppException;
import com.elara.accountservice.exception.UnAuthorizedException;
import com.elara.accountservice.repository.CompanyRepository;
import com.elara.accountservice.service.AuthenticationService;
import com.elara.accountservice.service.MessageService;
import com.elara.accountservice.util.HashUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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

    private String authServerClientId;

    private String serviceName;

    private final AuthenticationService authenticationService;

    private final CompanyRepository companyRepository;

    private final MessageService messageService;

    public AuthenticationInterceptor(AuthenticationService authenticationService, CompanyRepository companyRepository, MessageService messageService) {
        this.authenticationService = authenticationService;
        this.companyRepository = companyRepository;
        this.messageService = messageService;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("AuthenticationInterceptor::preHandle()");

        System.out.println("message: " + messageService.getMessage("Company.NotFound"));

        String clientId = request.getHeader("client-id");
        Company company = companyRepository.findByClientId(clientId);
        if (company == null) {
            throw new AppException(messageService.getMessage("Company.NotFound"));
        }

        if (!EntityStatus.Enabled.name().equals(company.getStatus())) {
            throw new AppException(messageService.getMessage("Company.Account.Disabled"));
        }

        RequestUtil.setAuthToken(new AuthToken());
        RequestUtil.getAuthToken().setCompanyCode(company.getCompanyCode());
        RequestUtil.getAuthToken().setCompanyName(company.getCompanyName());

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        if (!isSecuredRoute(handlerMethod)) {
            return true;
        }

        //Call Authorization server with token forward by ApiGateway, then add the client id, and service as header
        //Authorization server will authenticate the token that it generated and return response back
        //Check if response is authenticated and has permission, allow to proceed

        String token = request.getHeader("Authorization");

        if (token == null || token.trim().equalsIgnoreCase("")) {
            throw new AppException(messageService.getMessage("Token.Required"));
        }
        token = token.replace("Bearer ", "");

        String permissionId = HashUtil.getHash(serviceName + handlerMethod.getMethod().getDeclaredAnnotation(Permission.class).value());
       TokenVerifyResponse result = authenticationService.verifyToken(TokenVerifyRequest.builder()
                       .token(token)
                       .serviceClientId(authServerClientId)
                       .permissionId(permissionId)
                       .build());

       if (!ResponseCode.SUCCESSFUL.getValue().equals(result.getResponseCode())) {
           throw new UnAuthorizedException(messageService.getMessage("Token.Invalid"));
       }

       RequestUtil.getAuthToken().setUsername(result.getData().getUsername());
       RequestUtil.getAuthToken().setUuid(result.getData().getLoginId());
       RequestUtil.getAuthToken().setEmail(result.getData().getEmail());
       RequestUtil.getAuthToken().setPhone(result.getData().getPhone());
       RequestUtil.getAuthToken().setPhoneVerified(result.getData().isPhoneVerified());
       RequestUtil.getAuthToken().setEmailVerified(result.getData().isEmailVerified());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("AuthenticationInterceptor::postHandle()");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("AuthenticationInterceptor::afterCompletion()");
    }

    private boolean isSecuredRoute(HandlerMethod handlerMethod) {
        return handlerMethod.getMethod().isAnnotationPresent(
            Permission.class);
    }

    public void setAuthServerClientId(String authServerClientId) {
        this.authServerClientId = authServerClientId;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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
