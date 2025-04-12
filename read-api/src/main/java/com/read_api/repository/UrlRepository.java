package com.read_api.repository;

import com.read_api.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UrlRepository extends JpaRepository<Url, String> {
}
