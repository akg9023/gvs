package com.voice.dbRegistration.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;

@Service
public class AWSS3Service {

   @Value("${s3.bucket.name}")
   private String bucket;

    // Initialize the AmazonS3 client object
    private final AmazonS3 s3client = AmazonS3ClientBuilder.standard()
       .withRegion("us-east-1")
       .build();

    // Method to upload a file to S3 bucket
    public String uploadFile(MultipartFile file) {
       String fileName = file.getOriginalFilename();
       String bucketName = bucket;
       ObjectMetadata metadata = new ObjectMetadata();
       metadata.setContentLength(file.getSize());
       try (InputStream inputStream = file.getInputStream()) {
          s3client.putObject(bucketName, fileName, inputStream, metadata);
          return s3client.getUrl(bucketName, fileName).toExternalForm();
       } catch (IOException e) {
          throw new RuntimeException("Error uploading file to S3", e);
       }
    }

 }