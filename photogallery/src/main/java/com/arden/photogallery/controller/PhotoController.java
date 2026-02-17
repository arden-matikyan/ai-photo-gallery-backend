package com.arden.photogallery.controller;

import com.arden.photogallery.model.Photo;
import com.arden.photogallery.service.OpenAIService;
import com.arden.photogallery.service.PhotoService;
import com.arden.photogallery.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.arden.photogallery.service.S3Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;



import java.util.List;




@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
@CrossOrigin
public class PhotoController {

    private final PhotoService photoService;
    private final S3Service s3Service;
    private final AIService aiService;
    private final OpenAIService openAIService;


    @PostMapping
    public Photo createPhoto(@RequestBody Photo photo) {
        return photoService.savePhoto(photo);
    }

    @GetMapping
    public List<Photo> getAllPhotos() {
        return photoService.getAllPhotos();
    }

    @GetMapping("/{id:\\d+}")
    public Photo getPhoto(@PathVariable Long id) {
        return photoService.getPhoto(id);
    }


    @DeleteMapping("/{id}")
    public void deletePhoto(@PathVariable Long id) {
        photoService.deletePhoto(id);
    }

    @GetMapping("/search")
    public List<Photo> search(@RequestParam String q) {
        return photoService.search(q);
    }

    @PostMapping("/upload")
    public Photo uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String title
    ) throws IOException {

        // Upload to S3
        String key = s3Service.uploadFile(
                file.getOriginalFilename(),
                file.getBytes()
        );

        // extract object key from URL
        String objectKey = key.substring(key.lastIndexOf("/") + 1);

        // generate AI tags
        String tags = aiService.generateTags(objectKey);

        // generate caption
        String caption;

        try {
            caption = openAIService.generateCaption(tags);
        } catch (Exception e) {
            caption = "AI caption unavailable";
        }

        // Save photo
        Photo photo = new Photo();
        photo.setTitle(title);
        photo.setS3Url(key);
        photo.setTags(tags);
        photo.setDescription(caption);


        return photoService.savePhoto(photo);
    }



}

