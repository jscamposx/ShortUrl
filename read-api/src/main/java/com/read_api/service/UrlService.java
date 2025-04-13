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


    @Cacheable(value = "longUrls", key = "#shortUrl")
    public String getLongUrl(String shortUrl) {
        System.out.println(">>> Buscando en BD para shortUrl: " + shortUrl);
        return urlRepository.findById(shortUrl)
                .map(Url::getLongUrl)
                .orElseThrow(() -> new RuntimeException("URL no encontrada"));


    }

}
