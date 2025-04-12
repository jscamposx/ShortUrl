package com.write_api.repository;

import com.write_api.model.Url;

import org.springframework.data.jpa.repository.JpaRepository;



public interface UrlRepository extends JpaRepository<Url, String> {
    boolean existsByShortId(String shortId);
}
