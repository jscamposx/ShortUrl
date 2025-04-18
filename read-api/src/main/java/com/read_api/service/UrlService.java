package com.read_api.service;

import com.read_api.exceptions.ResourceNotFoundException; // Importar
import com.read_api.model.Url;
import com.read_api.repository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private static final Logger log = LoggerFactory.getLogger(UrlService.class);
    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Cacheable(value = "longUrls", key = "#shortId")
    public String getLongUrl(String shortId) {
        log.info("Buscando URL larga para shortId: {}", shortId);
        Url url = urlRepository.findById(shortId)
                .orElseThrow(() -> {
                    log.warn("URL no encontrada en BD para shortId: {}", shortId);
                    return new ResourceNotFoundException("Url", "shortId", shortId);
                });
        log.info("URL larga encontrada para shortId {}: {}", shortId, url.getLongUrl());
        return url.getLongUrl();
    }
}