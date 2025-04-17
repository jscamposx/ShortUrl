package com.read_api.service;

import com.read_api.model.Url;
import com.read_api.repository.UrlRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Cacheable(value = "longUrls", key = "#shortId")
    public String getLongUrl(String shortId) {
        try {
            String longUrl = urlRepository.findById(shortId)
                    .map(Url::getLongUrl)
                    .orElseThrow(() -> new RuntimeException("URL no encontrada en BD para: " + shortId));
            return longUrl;
        } catch (Exception e) {
            throw e;
        }

    }
}