package com.arden.photogallery.controller;

import com.arden.photogallery.model.Photo;
import com.arden.photogallery.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
@CrossOrigin
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping
    public Photo createPhoto(@RequestBody Photo photo) {
        return photoService.savePhoto(photo);
    }

    @GetMapping
    public List<Photo> getAllPhotos() {
        return photoService.getAllPhotos();
    }

    @GetMapping("/{id}")
    public Photo getPhoto(@PathVariable Long id) {
        return photoService.getPhoto(id);
    }

    @DeleteMapping("/{id}")
    public void deletePhoto(@PathVariable Long id) {
        photoService.deletePhoto(id);
    }

    @GetMapping("/search")
    public List<Photo> search(@RequestParam String q) {
        return photoService.searchByTag(q);
    }
}
