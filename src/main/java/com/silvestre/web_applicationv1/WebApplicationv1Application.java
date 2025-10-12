package com.silvestre.web_applicationv1;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.silvestre.web_applicationv1")
@EntityScan(basePackages = "com.silvestre.web_applicationv1.entity")
@EnableJpaRepositories(basePackages = "com.silvestre.web_applicationv1.repository")
public class WebApplicationv1Application {

	public static void main(String[] args) {


		Dotenv dotenv = Dotenv.configure().load();

		SpringApplication.run(WebApplicationv1Application.class, args);
	}

}
