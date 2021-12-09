package com.nttdata.retire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class RetireServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetireServiceApplication.class, args);
	}

}
