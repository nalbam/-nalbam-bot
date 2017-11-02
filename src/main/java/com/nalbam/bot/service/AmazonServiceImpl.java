package com.nalbam.bot.service;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class AmazonServiceImpl implements AmazonService {

    @Value("${aws.bucket}")
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
    public Map<String, Object> upload(final MultipartFile file) {
        final String exe = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        final String key = UUID.randomUUID().toString() + "." + exe;

        final Map<String, Object> result = new HashMap<>();
        result.put("contentType", file.getContentType());
        result.put("filename", file.getOriginalFilename());
        result.put("size", file.getSize());

        try {
            final ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            final PutObjectRequest request = new PutObjectRequest(this.bucket, key, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            this.amazonS3.putObject(request);

            log.info("## upload : https://s3.amazonaws.com/{}/{}", this.bucket, key);

            result.put("bucket", this.bucket);
            result.put("key", key);
            result.put("url", String.format("https://s3.amazonaws.com/%s/%s", this.bucket, key));
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private Image getImage(final String key) {
        return getImage(new S3Object().withBucket(this.bucket).withName(key));
    }

    private Image getImage(final S3Object s3Object) {
        return new Image().withS3Object(s3Object);
    }

}
