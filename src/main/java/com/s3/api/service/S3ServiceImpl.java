package com.s3.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

@Service
public class S3ServiceImpl implements IS3Service {

    @Value("${spring.destination.folder}")
    private String destinationFolder;

    @Autowired
    private S3Client s3Client;

    @Override
    public String createBucket(String bucketName) {
        CreateBucketResponse response = this.s3Client.createBucket(bucketBuilder -> bucketBuilder.bucket(bucketName));
        return "Bucket created: " + response.location();
    }

    @Override
    public String checkBucketExists(String bucketName) {
        try {
            this.s3Client.headBucket(headBucket -> headBucket.bucket(bucketName));
            return "El Bucket " + bucketName + " existe.";
        }catch (S3Exception e){
            return "El bucket "+bucketName+" no existe";
        }

    }

    @Override
    public List<String> listBuckets() {
        ListBucketsResponse listBucketsResponse = this.s3Client.listBuckets();

        if (listBucketsResponse.hasBuckets()) {
            return listBucketsResponse.buckets()
                    .stream().map(Bucket::name).toList();
        }else{
            return List.of();
        }
    }

    @Override
    public Boolean uploadFile(String bucketName, String key, Path fileLocation) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        PutObjectResponse putObjectResponse = this.s3Client.putObject(putObjectRequest,fileLocation);
        return putObjectResponse.sdkHttpResponse().isSuccessful();
    }

    @Override
    public void downloadFile(String bucketName, String key) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> getObjectResponseResponseBytes = this.s3Client.getObjectAsBytes(getObjectRequest);

        String fileName;
        if (key.contains("/")) {
            fileName = key.substring(key.lastIndexOf('/'));
        }else {
            fileName = key;
        }

        String filePath = Paths.get(destinationFolder,fileName).toString();

        File file = new File(filePath);
        file.getParentFile().mkdir();

        try(FileOutputStream fos = new FileOutputStream(file)){
            fos.write(getObjectResponseResponseBytes.asByteArray());
        }catch (IOException e){
            throw new IOException("Error al descargar el archivo. Causa: " +e.getMessage());
        }
    }

    @Override
    public void generatePresignedUploadUrl(String bucketName, String key, Duration duration) {

    }

    @Override
    public void generatePresignedDownloadUrl(String bucketName, String key, Duration duration) {

    }
}
