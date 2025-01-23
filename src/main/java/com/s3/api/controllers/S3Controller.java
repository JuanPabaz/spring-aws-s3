package com.s3.api.controllers;

import com.s3.api.service.IS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    @GetMapping("/list")
    public ResponseEntity<List<String>> getAllBuckets() {
        return ResponseEntity.ok(s3Service.listBuckets());
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam String bucketName,
                                             @RequestParam String key,
                                             @RequestPart MultipartFile file) throws IOException {
        try {
            Path staticDir = Paths.get(destinationFolder);

            if (!Files.exists(staticDir)) {
                Files.createDirectory(staticDir);
            }

            Path filePath = staticDir.resolve(file.getOriginalFilename());
            Path finalPath = Files.write(filePath, file.getBytes());

            Boolean result = this.s3Service.uploadFile(bucketName, key, finalPath);

            if (result) {
                Files.delete(finalPath);
                return ResponseEntity.ok("Archivo guardado exitosamente");
            }else {
                return ResponseEntity.internalServerError().body("Error al guardar el archivo");
            }
        }catch (IOException e){
            throw new IOException("Error al procesar al archivo");
        }
    }

    @GetMapping("/download")
    public ResponseEntity<String> downloadFile(@RequestParam String bucketName,@RequestParam String key) throws IOException {
        this.s3Service.downloadFile(bucketName,key);
        return ResponseEntity.ok("Archivo descargado exitosamente");
    }

}
