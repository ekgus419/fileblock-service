package com.example.fileblock.file.controller;

import com.example.fileblock.file.service.FileService;
import com.example.fileblock.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ApiResponse<?> uploadFile(@RequestParam("file") MultipartFile file) {
        fileService.validateUpload(file);
        return ApiResponse.success("file uploaded successfully");
    }
}
