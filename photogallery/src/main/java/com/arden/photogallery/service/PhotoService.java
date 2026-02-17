package com.arden.photogallery.service;

import com.arden.photogallery.model.Photo;
import com.arden.photogallery.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
// @Service: . It’s primarily a semantic marker for the service layer (business logic), but functionally identical to @Component

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;

    public Photo savePhoto(Photo photo) {
        photo.setCreatedAt(LocalDateTime.now());
        return photoRepository.save(photo);
    }

    public List<Photo> getAllPhotos() {
        return photoRepository.findAll();
    }

    public Photo getPhoto(Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Photo not found"));
    }

    public void deletePhoto(Long id) {
        photoRepository.deleteById(id);
    }

    /* 
    public List<Photo> searchByTag(String tag) {
        return photoRepository.findByTagsContainingIgnoreCase(tag);
    }
    */ 

    public List<Photo> search(String query) {

        if (query == null || query.isBlank()) {
            return List.of();
        }

        String[] keywords = query.toLowerCase().split(" ");

        return photoRepository.findAll()
                .stream()
                .filter(photo -> {

                    String tags = photo.getTags();

                    if (tags == null || tags.isBlank()) {
                        return false;
                    }

                    String lowerTags = tags.toLowerCase();

                    for (String keyword : keywords) {
                        if (lowerTags.contains(keyword)) {
                            return true;
                        }
                    }

                    return false;
                })
                .toList();
    }


}
