package com.read_api;

import jakarta.annotation.PostConstruct; // Importar
import org.slf4j.Logger; // Importar
import org.slf4j.LoggerFactory; // Importar
import org.springframework.beans.factory.annotation.Autowired; // Importar
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.env.Environment; // Importar

@EnableDiscoveryClient
@SpringBootApplication
@EnableCaching
public class ReadApiApplication {

	// Añadir logger
	private static final Logger log = LoggerFactory.getLogger(ReadApiApplication.class);

	// Inyectar el Environment
	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(ReadApiApplication.class, args);
	}

	// Método que se ejecuta después de la inicialización
	@PostConstruct
	public void logRedisHost() {
		String redisHost = environment.getProperty("spring.redis.host");
		log.info(">>> [CONFIG CHECK] spring.redis.host = {}", redisHost);
	}
}