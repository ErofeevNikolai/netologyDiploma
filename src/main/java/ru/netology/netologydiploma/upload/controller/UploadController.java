package ru.netology.netologydiploma.upload.controller;

import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.netologydiploma.security.exception.ErrorGettingFileListException;
import ru.netology.netologydiploma.security.exception.ErrorUploadFileException;
import ru.netology.netologydiploma.upload.DTO.NewFileName;
import ru.netology.netologydiploma.upload.service.UploadService;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class UploadController {
    UploadService uploadService;

    @PostMapping("/file")
    public ResponseEntity<?> uploadFileToServer(
            @RequestParam("filename") String filename,
            @RequestHeader("auth-token") String token,
            MultipartFile file) throws ErrorUploadFileException {
        uploadService.postUpload(file, filename, token);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(
            @RequestParam("filename") String filename,
            @RequestHeader("auth-token") String token) throws IOException {

        uploadService.deleteFile(token, filename);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> dowloadFileFromCloud(
            @RequestParam("filename") String filename,
            @RequestHeader("auth-token") String token) throws IOException, ErrorUploadFileException {

        Resource resource =  uploadService.downloadFile(token, filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(resource);
    }

    //редактирование имени файла
    @PutMapping("/file")
    public ResponseEntity<?> editFileName(
            @RequestParam("filename") String filename,
            @RequestHeader("auth-token") String token,
            @RequestBody NewFileName newfileName) throws ErrorUploadFileException {

       uploadService.editFileName(token, filename, newfileName.getFilename());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    private ResponseEntity<?> getAllFiles(
            @RequestParam("limit") Integer limit,
            @RequestHeader("auth-token") String token) throws ErrorGettingFileListException {
        System.out.println(limit);
        return ResponseEntity.ok(uploadService.getAllFiles(token, limit));
    }
}
