package com.nalbam.bot.service;

import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.RecognizeCelebritiesResult;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.web.multipart.MultipartFile;

public interface AmazonService {

    DetectFacesResult detect(String key);

    RecognizeCelebritiesResult celebrity(String key);

    PutObjectResult upload(MultipartFile multipartFile);

}
