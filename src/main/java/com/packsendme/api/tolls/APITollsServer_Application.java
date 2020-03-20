package com.packsendme.api.tolls;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class APITollsServer_Application {

	public static void main(String[] args) {
		SpringApplication.run(APITollsServer_Application.class, args);
	}
}

