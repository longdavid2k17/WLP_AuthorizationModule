package pl.com.kantoch.authorizationmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AuthorizationModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationModuleApplication.class, args);
	}

}
