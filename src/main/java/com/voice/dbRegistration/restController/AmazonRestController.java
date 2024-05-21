package com.voice.dbRegistration.restController;

import com.voice.dbRegistration.service.AWSS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/image")
public class AmazonRestController {

    @Autowired
    private AWSS3Service s3Service;

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
       return s3Service.uploadFile(file);
    }
}