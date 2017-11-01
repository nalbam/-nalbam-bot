package com.nalbam.bot.controller;

import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.RecognizeCelebritiesResult;
import com.nalbam.bot.service.AmazonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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
    public Map<String, Object> upload(@RequestParam final MultipartFile file) {
        return this.amazonService.upload(file);
    }

}
