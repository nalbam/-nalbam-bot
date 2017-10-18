package com.nalbam.bot.controller;

import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.nalbam.bot.service.AmazonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/face")
public class FaceController {

    @Autowired
    private AmazonService amazonService;

    @GetMapping
    public SearchFacesByImageResult search(@RequestParam final String key) {
        return this.amazonService.searchFaces(key);
    }

    @PostMapping
    public PutObjectResult upload(@RequestParam final MultipartFile multipartFile) {
        return this.amazonService.upload(multipartFile);
    }

}
