package com.example.fileblock.extension.service;

import com.example.fileblock.extension.domain.CustomExtension;
import com.example.fileblock.extension.domain.FixedExtension;
import com.example.fileblock.extension.repository.CustomExtensionRepository;
import com.example.fileblock.extension.repository.FixedExtensionRepository;
import com.example.fileblock.global.exception.DuplicateExtensionException;
import com.example.fileblock.global.exception.LimitExceededException;
import com.example.fileblock.global.exception.NotFoundException;
import com.example.fileblock.global.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtensionService {

    private final FixedExtensionRepository fixedExtensionRepository;
    private final CustomExtensionRepository customExtensionRepository;

    // ===== 고정 확장자 =====

    public List<FixedExtension> getFixedExtensions() {
        return fixedExtensionRepository.findAll();
    }

    public FixedExtension updateFixedExtension(Long seq, boolean isBlocked) {
        FixedExtension ext = fixedExtensionRepository.findById(seq)
                .orElseThrow(() -> new NotFoundException("Fixed extension not found"));
        ext.toggleBlock(isBlocked);
        return fixedExtensionRepository.save(ext);
    }

    // ===== 커스텀 확장자 =====

    public List<CustomExtension> getCustomExtensions() {
        return customExtensionRepository.findAll();
    }

    public CustomExtension addCustomExtension(String extension) {
        String normalized = normalize(extension);

        if (normalized.length() > 20) {
            throw new ValidationException("Extension must be at most 20 characters");
        }

        if (customExtensionRepository.count() >= 200) {
            throw new LimitExceededException("Maximum of 200 custom extensions allowed");
        }

        if (customExtensionRepository.existsByExtension(normalized)
                || fixedExtensionRepository.existsByExtension(normalized)) {
            throw new DuplicateExtensionException("Extension '" + normalized + "' already exists");
        }

        return customExtensionRepository.save(CustomExtension.of(normalized));
    }

    public void removeCustomExtension(Long seq) {
        if (!customExtensionRepository.existsById(seq)) {
            throw new NotFoundException("Custom extension not found");
        }
        customExtensionRepository.deleteById(seq);
    }

    // ===== 유틸 =====

    private String normalize(String ext) {
        return ext.toLowerCase().replace(".", "").trim();
    }
}
