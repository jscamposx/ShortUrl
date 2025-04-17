package com.write_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "url")
public class Url {

    @Id
    @Column(name = "short_id")
    private String shortId;

    @Column(nullable = false)
    private String longUrl;

    @Column(nullable = false)
    private LocalDate createdDate;



}
