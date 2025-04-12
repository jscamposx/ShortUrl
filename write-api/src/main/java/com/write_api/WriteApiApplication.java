package com.write_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class WriteApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WriteApiApplication.class, args);
	}

}
