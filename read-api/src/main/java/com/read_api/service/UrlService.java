package com.read_api.service;

import com.read_api.model.Url;
import com.read_api.repository.UrlRepository;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String getLongUrl(String shortUrl) {
        return urlRepository.findById(shortUrl)
                .map(Url::getLongUrl)
                .orElseThrow(() -> new RuntimeException("URL no encontrada"));
    }
}
