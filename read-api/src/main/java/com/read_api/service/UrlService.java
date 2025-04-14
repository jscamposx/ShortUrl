package com.read_api.service;

import com.read_api.model.Url;
import com.read_api.repository.UrlRepository;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    // Añade un logger
    private static final Logger log = LoggerFactory.getLogger(UrlService.class);

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }


    @Cacheable(value = "longUrls", key = "#shortUrl")
    public String getLongUrl(String shortUrl) {
        // Log ANTES de la lógica principal (antes de la caché/DB)
        log.info(">>> [Cacheable] Intentando obtener longUrl para shortUrl: {}", shortUrl);
        try {
            // Esta es la lógica que se ejecuta SI NO está en caché
            log.info(">>> [Cache Miss] Buscando en BD para shortUrl: {}", shortUrl);
            String longUrl = urlRepository.findById(shortUrl)
                    .map(Url::getLongUrl)
                    // Modifica la excepción para incluir el shortUrl
                    .orElseThrow(() -> new RuntimeException("URL no encontrada en BD para: " + shortUrl));
            log.info(">>> [DB Hit] Encontrado en BD para shortUrl: {}", shortUrl);
            return longUrl;
        } catch (Exception e) {
            // Log si hay cualquier error durante la búsqueda en BD
            log.error(">>> Error durante getLongUrl (búsqueda BD) para shortUrl: {} - Exception: {}", shortUrl, e.getMessage());
            throw e; // Relanza la excepción original
        }
        // Si el @Cacheable lanza la excepción Redis, no llegaremos a los logs de "Cache Miss" o "DB Hit".
    }
}