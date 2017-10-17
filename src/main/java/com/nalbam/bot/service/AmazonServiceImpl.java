package com.nalbam.bot.service;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AmazonServiceImpl implements AmazonService {

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private AmazonRekognition amazonRekognition;

    @Override
    public SearchFacesByImageResult searchFaces(final String key) {
        final String collectionId = UUID.randomUUID().toString();

        final Image image = getImage(this.bucket, key);

        final Float threshold = 70F;
        final Integer maxFaces = 3;

        final SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
                .withCollectionId(collectionId)
                .withImage(image)
                .withFaceMatchThreshold(threshold)
                .withMaxFaces(maxFaces);

        return this.amazonRekognition.searchFacesByImage(searchFacesByImageRequest);
    }

    @Override
    public PutObjectResult upload(final MultipartFile multipartFile) {
        final File file = getFile(multipartFile);

        return upload(file);
    }

    private PutObjectResult upload(final File file) {
        final String exe = FilenameUtils.getExtension(file.getName()).toLowerCase();
        final String key = UUID.randomUUID().toString() + "." + exe;

        final PutObjectRequest request = new PutObjectRequest(this.bucket, key, file);

        return this.amazonS3.putObject(request);
    }

    private File getFile(final MultipartFile multipart) {
        final File file = new File(multipart.getOriginalFilename());
        try {
            multipart.transferTo(file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private Image getImage(final String bucket, final String key) {
        return getImage(new S3Object().withBucket(bucket).withName(key));
    }

    private Image getImage(final S3Object s3Object) {
        return new Image().withS3Object(s3Object);
    }

}
