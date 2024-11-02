package com.roadmap.blog_project.controllers;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/static/images")
public class FileController
{

    private static final Map<String, MediaType> MEDIA_TYPE_MAP = new HashMap<>();

    static {
        MEDIA_TYPE_MAP.put("jpg", MediaType.IMAGE_JPEG);
        MEDIA_TYPE_MAP.put("jpeg", MediaType.IMAGE_JPEG);
        MEDIA_TYPE_MAP.put("png", MediaType.IMAGE_PNG);
        MEDIA_TYPE_MAP.put("gif", MediaType.IMAGE_GIF);
        MEDIA_TYPE_MAP.put("ico", MediaType.parseMediaType("image/x-icon"));
    }

    //root path for image files
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable("filename") String filename) throws IOException {
        Resource resource = new ClassPathResource("static/images/" + filename);

        if (resource.exists() && resource.isFile())
        {
            String extension = getFileExtension(filename);

            MediaType mediaType = MEDIA_TYPE_MAP.getOrDefault(
                    extension, MediaType.APPLICATION_OCTET_STREAM
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Adjust content type based on image format
                    .body(resource);
        } else {
            throw new FileNotFoundException("Image not found: " + filename);
        }
    }

    private String getFileExtension(String filename)
    {
        int dotIndex = filename.indexOf('.');

        return (dotIndex >= 0) ? filename.substring(dotIndex + 1).toLowerCase() : "";
    }


}
