package com.read_api.controller;

import com.read_api.service.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/read")
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/{shortId}")
    public ResponseEntity<String> getLongUrl(@PathVariable String shortId) {
        String longUrl = urlService.getLongUrl(shortId);
        return ResponseEntity.ok(longUrl);
    }
}

