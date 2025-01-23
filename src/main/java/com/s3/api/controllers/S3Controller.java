package com.s3.api.controllers;

import com.s3.api.service.IS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Value("${spring.destination.folder}")
    private String destinationFolder;

    @Autowired
    private IS3Service s3Service;

    @PostMapping("/create")
    public ResponseEntity<String> createBucket(@RequestParam String bucketName) {
        return ResponseEntity.ok(s3Service.createBucket(bucketName));
    }

    @GetMapping("/check/{bucketName}")
    public ResponseEntity<String> checkBucket(@PathVariable String bucketName) {
        return ResponseEntity.ok(s3Service.checkBucketExists(bucketName));
    }

}
