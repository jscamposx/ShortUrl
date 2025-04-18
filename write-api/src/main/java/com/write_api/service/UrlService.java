package com.write_api.service;

import com.write_api.exceptions.DatabaseOperationException;
import com.write_api.exceptions.InvalidUrlFormatException;
import com.write_api.exceptions.ShortIdCollisionException;
import com.write_api.model.Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import com.write_api.repository.UrlRepository;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.regex.Pattern; // Importar para regex

@Service
public class UrlService {
    private final UrlRepository repository;
    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int ID_LENGTH = 5;
    private static final int MAX_GENERATE_ATTEMPTS = 10;


    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?://)?" +
                    "([\\da-z.-]+)\\." +
                    "([a-z.]{2,6})" +
                    "([/\\w .-]*)*/?$"
            , Pattern.CASE_INSENSITIVE);

    private static final Logger log = LoggerFactory.getLogger(UrlService.class);


    public UrlService(UrlRepository repository) {
        this.repository = repository;
    }

    private String generateShortId() {
        String id = null;
        int attempts = 0;
        boolean exists;

        do {
            if (attempts >= MAX_GENERATE_ATTEMPTS) {
                log.error("Failed to generate a unique short ID after {} attempts.", MAX_GENERATE_ATTEMPTS);
                throw new ShortIdCollisionException("Could not generate a unique short ID after " + MAX_GENERATE_ATTEMPTS + " attempts.");
            }
            id = randomBase62(ID_LENGTH);
            try {
                exists = repository.existsByShortId(id);
            } catch (DataAccessException e) {
                log.error("Database error checking for short ID existence: {}", id, e);
                throw new DatabaseOperationException("Error checking short ID uniqueness", e);
            }
            attempts++;

        } while (exists);

        log.info("Generated unique short ID: {} after {} attempts", id, attempts);
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
        if (longUrl == null || longUrl.trim().isEmpty()) {
            log.warn("Attempted to shorten an empty or null URL.");
            throw new InvalidUrlFormatException("URL cannot be empty.");
        }
        longUrl = longUrl.trim();
        String shortId = generateShortId();
        Url url = new Url();
        url.setShortId(shortId);
        url.setLongUrl(longUrl);
        url.setCreatedDate(LocalDate.now());

        try {
            log.info("Attempting to save URL: ShortID={}, LongURL={}", shortId, longUrl);
            Url savedUrl = repository.save(url);
            log.info("Successfully saved URL with ShortID: {}", savedUrl.getShortId());
            return savedUrl;
        } catch (DataAccessException e) {
            log.error("Database error saving URL mapping: ShortID={}, LongURL={}", shortId, longUrl, e);
            throw new DatabaseOperationException("Failed to save URL mapping", e);
        }
    }
}