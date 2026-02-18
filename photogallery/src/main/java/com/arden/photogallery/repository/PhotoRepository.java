package com.arden.photogallery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arden.photogallery.model.Photo;




public interface PhotoRepository extends JpaRepository<Photo, Long> {

    // TODO: replace search with primary subject, mood embedding, or descrptions? 

    // List<Photo> findByTagsContainingIgnoreCase(String tag);

}
