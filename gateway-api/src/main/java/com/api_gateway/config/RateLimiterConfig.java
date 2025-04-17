package com.api_gateway.config;


import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.Objects;
// Opcional: si vas a limitar por usuario autenticado
// import java.security.Principal;

@Configuration
public class RateLimiterConfig {

    /**
     * Resuelve la clave para el rate limiting basado en la dirección IP del cliente.
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            // Intenta obtener la IP desde el header X-Forwarded-For (común detrás de proxies/load balancers)
            String forwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isEmpty()) {
                // Toma la primera IP si hay varias separadas por coma
                return Mono.just(forwardedFor.split(",")[0].trim());
            }
            // Si no está el header, usa la dirección remota directa (puede ser la del proxy)
            return Mono.just(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
        };
    }

    /*
    // --- Alternativas ---

    // Ejemplo: Resolver por un Header específico (ej. X-Client-ID)
    @Bean
    public KeyResolver headerKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getHeaders().getFirst("X-Client-ID"));
    }

    // Ejemplo: Resolver por usuario autenticado (requiere Spring Security)
    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal().map(Principal::getName).defaultIfEmpty("anonymous");
    }
    */
}