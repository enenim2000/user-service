package com.elara.accountservice.auth;

import com.elara.accountservice.repository.CompanyRepository;
import com.elara.accountservice.service.AuthenticationService;
import com.elara.accountservice.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    MessageService messageService;

    @Value("${oauth.server.client-id}")
    String authServerClientId;

    @Value("${spring.application.name}")
    String serviceName;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        AuthenticationInterceptor authenticationInterceptor = new AuthenticationInterceptor(authenticationService, companyRepository, messageService);
        authenticationInterceptor.setServiceName(serviceName);
        authenticationInterceptor.setAuthServerClientId(authServerClientId);
        registry.addInterceptor(authenticationInterceptor)
                .excludePathPatterns("/swagger-ui/**")
                .excludePathPatterns("/v3/api-docs/**");
    }
}
