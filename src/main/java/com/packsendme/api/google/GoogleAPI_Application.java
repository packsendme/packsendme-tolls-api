package com.packsendme.api.google;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EnableAutoConfiguration
public class GoogleAPI_Application {

	public static void main(String[] args) {
		SpringApplication.run(GoogleAPI_Application.class, args);
	}
}

