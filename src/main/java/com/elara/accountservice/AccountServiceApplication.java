package com.elara.accountservice;

import com.elara.accountservice.dto.request.SyncPermissionRequest;
import com.elara.accountservice.service.ApplicationService;
import com.elara.accountservice.service.CompanyService;
import com.elara.accountservice.service.PermissionService;
import com.elara.accountservice.util.PasswordEncoder;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@EnableJpaRepositories({"com.elara.accountservice.repository"})
@ComponentScan({"com.elara.accountservice"})
@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication
public class AccountServiceApplication extends SpringBootServletInitializer implements CommandLineRunner {

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	ApplicationService applicationService;

	@Autowired
	CompanyService companyService;

	@Autowired
	PermissionService permissionService;

	@Autowired
	RequestMappingHandlerMapping requestMappingHandlerMapping;

	@Value("${app.public-key}")
	public String publicKey;

	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AccountServiceApplication.class);
	}

	@Override
	public void run(String... args) throws Exception {

		SyncPermissionRequest syncPermissionRequest = permissionService.generatePermissionRequest(requestMappingHandlerMapping);
		permissionService.syncApplicationPermission(syncPermissionRequest);
		//System.out.println("SyncPermissionRequest: " + new Gson().toJson(syncPermissionRequest));
		/*Map<String, String> keys = RSAUtil.generateKeyPair();
		System.out.println("public-key: " + keys.get("public-key"));
		System.out.println("private-key: " + keys.get("private-key"));*/

	}
}
