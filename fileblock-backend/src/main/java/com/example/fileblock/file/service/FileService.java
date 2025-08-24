package com.example.fileblock.file.service;

import com.example.fileblock.extension.domain.FixedExtension;
import com.example.fileblock.extension.repository.CustomExtensionRepository;
import com.example.fileblock.extension.repository.FixedExtensionRepository;
import com.example.fileblock.global.exception.BlockedExtensionException;
import com.example.fileblock.global.exception.InvalidFileTypeException;
import com.example.fileblock.global.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FixedExtensionRepository fixedExtensionRepository;
    private final CustomExtensionRepository customExtensionRepository;

    public void validateUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File is required");
        }

        // 1. 확장자 추출
        String filename = Optional.ofNullable(file.getOriginalFilename())
                .orElseThrow(() -> new ValidationException("Filename is missing"));
        String extension = extractExtension(filename);

        // 2. 확장자 차단 여부 확인
        boolean blocked = fixedExtensionRepository.findByExtension(extension)
                .map(FixedExtension::getIsBlocked)
                .orElse(false);

        if (blocked || customExtensionRepository.existsByExtension(extension)) {
            throw new BlockedExtensionException("The extension '" + extension + "' is not allowed");
        }

        // 3. MIME 타입 / Magic Number 검사 (간단히 예시)
        try {
            String mimeType = file.getContentType();
            if (mimeType == null || mimeType.equals("application/octet-stream")) {
                throw new InvalidFileTypeException("File type does not match extension");
            }
        } catch (Exception e) {
            throw new InvalidFileTypeException("File type check failed");
        }
    }

    private String extractExtension(String filename) {
        int dotIdx = filename.lastIndexOf(".");
        if (dotIdx == -1) {
            throw new ValidationException("File must have an extension");
        }
        return filename.substring(dotIdx + 1).toLowerCase();
    }
}
