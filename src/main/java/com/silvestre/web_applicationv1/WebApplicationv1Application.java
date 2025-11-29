package com.silvestre.web_applicationv1;

import com.silvestre.web_applicationv1.service.RedisTestService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.silvestre.web_applicationv1")
@EntityScan(basePackages = "com.silvestre.web_applicationv1.entity")
@EnableJpaRepositories(basePackages = "com.silvestre.web_applicationv1.repository")
@EnableScheduling
public class WebApplicationv1Application {

	public static void main(String[] args) {
		SpringApplication.run(WebApplicationv1Application.class, args);
	}
}
