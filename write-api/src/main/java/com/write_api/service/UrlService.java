package com.write_api.service;

import com.write_api.model.Url;
import org.springframework.stereotype.Service;
import com.write_api.repository.UrlRepository;
import java.security.SecureRandom;
import java.time.LocalDate;

@Service
public class UrlService {
    private final UrlRepository repository;
    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int ID_LENGTH = 5;

    public UrlService(UrlRepository repository) {
        this.repository = repository;
    }

    public String generateShortId() {
        String id;
        do {
            id = randomBase62(ID_LENGTH);
        } while (repository.existsByShortId(id));
        return id;
    }

    private String randomBase62(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(BASE62.charAt(random.nextInt(BASE62.length())));
        }
        return sb.toString();
    }

    public Url saveUrl(String longUrl) {
        Url url = new Url();
        url.setShortId(generateShortId());
        url.setLongUrl(longUrl);
        url.setCreatedDate(LocalDate.now());
        return repository.save(url);
    }
}
