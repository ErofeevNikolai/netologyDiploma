package ru.netology.netologydiploma.upload.dto.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.netologydiploma.security.exception.ErrorGettingFileListException;
import ru.netology.netologydiploma.security.exception.ErrorUploadFileException;
import ru.netology.netologydiploma.upload.dto.NewFileName;
import ru.netology.netologydiploma.upload.service.UploadService;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class UploadController {
    UploadService uploadService;

    @PostMapping("/file")
    public ResponseEntity<?> uploadFileToServer(
            @RequestParam("filename") String filename,
            MultipartFile file) throws ErrorUploadFileException, IOException {
        uploadService.postUpload(file, filename);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam("filename") String filename) throws IOException, ErrorUploadFileException {
        uploadService.deleteFile(filename);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> dowloadFileFromCloud(@RequestParam("filename") String filename) throws IOException, ErrorUploadFileException {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(uploadService.downloadFile(filename));
    }

    //редактирование имени файла
    @PutMapping("/file")
    public ResponseEntity<?> editFileName(
            @RequestParam("filename") String filename,
            @RequestBody NewFileName newfileName) throws ErrorUploadFileException {
        uploadService.editFileName(filename, newfileName.getFilename());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    private ResponseEntity<?> getAllFiles(
            @RequestParam("limit") Integer limit,
            @RequestHeader("auth-token") String token) throws ErrorGettingFileListException {
        return ResponseEntity.ok(uploadService.getAllFiles(limit));
    }
}
