package com.nalbam.bot.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {

    @Value("${nalbam.aws.access_key}")
    private String access_key;

    @Value("${nalbam.aws.secret_key}")
    private String secret_key;

    @Value("${nalbam.aws.region}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(this.region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials()))
                .build();
    }

    @Bean
    public AmazonRekognition amazonRekognition() {
        return AmazonRekognitionClientBuilder.standard()
                .withRegion(Regions.fromName(this.region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials()))
                .build();
    }

    private AWSCredentials credentials() {
        return new BasicAWSCredentials(this.access_key, this.secret_key);
    }

}
