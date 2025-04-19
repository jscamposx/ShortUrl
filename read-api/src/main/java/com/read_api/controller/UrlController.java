package com.read_api.controller;

import com.read_api.service.UrlService;
import org.springframework.http.HttpHeaders; 
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI; 
import java.net.URISyntaxException; 

@RestController
@RequestMapping("/read")
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/{shortId}")
    public ResponseEntity<Void> redirectUrl(@PathVariable String shortId) {
        String longUrl = urlService.getLongUrl(shortId);
        String redirectUrl = longUrl;
        if (!redirectUrl.matches("^(?i)(https?://).*")) {
            redirectUrl = "http://" + redirectUrl;
        }
        URI locationUri;
        try {
            locationUri = new URI(redirectUrl);
        } catch (URISyntaxException e) {
            System.err.println("URL inválida almacenada para shortId " + shortId + ": " + redirectUrl);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(locationUri)
                .build();
    }
}
