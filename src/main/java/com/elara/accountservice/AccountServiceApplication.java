package com.elara.accountservice;

import com.elara.accountservice.service.ApplicationService;
import com.elara.accountservice.service.CompanyService;
import com.elara.accountservice.util.PasswordEncoder;
import com.elara.accountservice.util.RSAUtil;
import io.jsonwebtoken.impl.crypto.MacProvider;
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

import java.util.Map;

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

		Map<String, String> keys = RSAUtil.generateKeyPair();
		System.out.println("public-key: " + keys.get("public-key"));
		System.out.println("private-key: " + keys.get("private-key"));

		/*
				System.out.println("password: " + passwordEncoder.encode("Password@123"));

				System.out.println("company client id: " + RSAUtil.encrypt(UUID.randomUUID().toString(), publicKey));
				System.out.println("company client secret: " + RSAUtil.encrypt(UUID.randomUUID().toString(), publicKey));

				System.out.println("app client id: " + RSAUtil.encrypt(UUID.randomUUID().toString(), publicKey));
				System.out.println("app client secret: " + RSAUtil.encrypt(UUID.randomUUID().toString(), publicKey));
		*/
	}
}
