package com.elara.accountservice;

import com.elara.accountservice.util.RSAUtil;
import com.google.gson.Gson;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@EnableJpaRepositories("com.elara.userservice.repository")
@EntityScan("com.elara.userservice.domain")
@ComponentScan(basePackages = {"com.elara.userservice.domain"})
@EnableScheduling
@EnableDiscoveryClient
@Component
@SpringBootApplication
public class AccountServiceApplication extends SpringBootServletInitializer implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AccountServiceApplication.class);
	}

	@Override
	public void run(String... args) throws Exception {
		//System.out.println("Keys:::   " + new Gson().toJson(RSAUtil.generateKeyPair()));
	}
}
