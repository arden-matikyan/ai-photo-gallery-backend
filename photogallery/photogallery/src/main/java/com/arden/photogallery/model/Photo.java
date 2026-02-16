package com.arden.photogallery.model;


// JPA translates your Java object into SQL automatically
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


// @Entity (from javax.persistence or jakarta.persistence) marks the class as a JPA entity that will be persisted to a database table
// @Data (from Lombok) is a compile‑time code generator that creates getters, setters, toString, equals, hashCode, and a required‑args constructor for all non‑final fields
@Entity
@Data
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String s3Url;

    @Column(length = 2000)
    private String tags;

    private LocalDateTime createdAt;
}

