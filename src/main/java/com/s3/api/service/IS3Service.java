package com.s3.api.service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public interface IS3Service {

    String createBucket(String bucketName);

    String checkBucketExists(String bucketName);

    List<String> listBuckets();

    Boolean uploadFile(String bucketName, String key, Path fileLocation);

    void downloadFile(String bucketName, String key) throws IOException;

    void generatePresignedUploadUrl(String bucketName, String key, Duration duration);

    void generatePresignedDownloadUrl(String bucketName, String key, Duration duration);
}
