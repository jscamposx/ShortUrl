package com.write_api.controller;


import com.write_api.model.Url;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.write_api.service.UrlService;



@RestController

@RequestMapping("/api/write")
public class UrlController {
    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestParam String longUrl) {
        Url url = service.saveUrl(longUrl);
        return ResponseEntity.ok(url.getShortId());
    }
}