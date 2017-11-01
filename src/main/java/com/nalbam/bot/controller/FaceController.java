package com.nalbam.bot.controller;

import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.RecognizeCelebritiesResult;
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

    @GetMapping("/detect")
    public DetectFacesResult detect(@RequestParam final String key) {
        return this.amazonService.detect(key);
    }

    @GetMapping("/celebrity")
    public RecognizeCelebritiesResult celebrity(@RequestParam final String key) {
        return this.amazonService.celebrity(key);
    }

    @PostMapping("/upload")
    public PutObjectResult upload(final MultipartFile multipartFile) {
        return this.amazonService.upload(multipartFile);
    }

}
