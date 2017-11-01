package com.nalbam.bot.service;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class AmazonServiceImpl implements AmazonService {

    @Value("${nalbam.aws.bucket}")
    private String bucket;

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private AmazonRekognition amazonRekognition;

    @Override
    public DetectFacesResult detect(final String key) {
        final Image image = getImage(key);

        final DetectFacesRequest request = new DetectFacesRequest()
                .withImage(image)
                .withAttributes(Attribute.ALL);

        return this.amazonRekognition.detectFaces(request);
    }

    @Override
    public RecognizeCelebritiesResult celebrity(final String key) {
        final Image image = getImage(key);

        final RecognizeCelebritiesRequest request = new RecognizeCelebritiesRequest()
                .withImage(image);

        return this.amazonRekognition.recognizeCelebrities(request);
    }

    @Override
    public PutObjectResult upload(final MultipartFile multipartFile) {
        log.info("upload {}", multipartFile);

        final String exe = FilenameUtils.getExtension(multipartFile.getOriginalFilename()).toLowerCase();
        final String key = UUID.randomUUID().toString() + "." + exe;

        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());

        try {
            final PutObjectRequest request = new PutObjectRequest(this.bucket, key, multipartFile.getInputStream(), objectMetadata);

            return this.amazonS3.putObject(request);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Image getImage(final String key) {
        return getImage(new S3Object().withBucket(this.bucket).withName(key));
    }

    private Image getImage(final S3Object s3Object) {
        return new Image().withS3Object(s3Object);
    }

}
