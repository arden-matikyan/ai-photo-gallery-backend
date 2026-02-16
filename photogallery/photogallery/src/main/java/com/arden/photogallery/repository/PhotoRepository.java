package com.arden.photogallery.repository;

import com.arden.photogallery.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByTagsContainingIgnoreCase(String tag);

}
