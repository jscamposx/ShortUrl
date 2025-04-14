package com.read_api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.lang.NonNull; // O usa import jakarta.annotation.Nonnull; si es la dependencia que tienes

@Configuration
public class RedisConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    @Autowired
    private Environment environment;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // Lee las propiedades directamente del Environment
        String redisHost = environment.getProperty("spring.redis.host", "localhost"); // Default a localhost si no se encuentra
        int redisPort = environment.getProperty("spring.redis.port", Integer.class, 6379); // Default a 6379

        log.info(">>> [Explicit Redis Config] Configuring LettuceConnectionFactory with host: {}, port: {}", redisHost, redisPort);

        // Crea la configuración standalone (no cluster, no sentinel en tu caso)
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        // Si tuvieras contraseña, la añadirías aquí:
        // redisConfig.setPassword(environment.getProperty("spring.redis.password"));

        // Crea y devuelve la fábrica de conexiones Lettuce
        return new LettuceConnectionFactory(redisConfig);
    }
}