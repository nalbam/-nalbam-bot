package com.nalbam.bot.service;

import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.web.multipart.MultipartFile;

public interface AmazonService {

    SearchFacesByImageResult searchFaces(String key);

    PutObjectResult upload(MultipartFile multipartFile);

}
