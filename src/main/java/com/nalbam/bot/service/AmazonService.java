package com.nalbam.bot.service;

import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.RecognizeCelebritiesResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface AmazonService {

    DetectFacesResult detect(String key);

    RecognizeCelebritiesResult celebrity(String key);

    Map<String, Object> upload(MultipartFile file);

}
