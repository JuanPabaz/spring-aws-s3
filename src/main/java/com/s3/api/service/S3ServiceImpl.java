package com.s3.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.print.attribute.standard.Destination;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3ServiceImpl implements IS3Service {

    @Value("${spring.destination.folder}")
    private Destination destinationFolder;

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
        return null;
    }

    @Override
    public void downloadFile(String bucketName, String key) throws IOException {

    }

    @Override
    public void generatePresignedUploadUrl(String bucketName, String key, Duration duration) {

    }

    @Override
    public void generatePresignedDownloadUrl(String bucketName, String key, Duration duration) {

    }
}
