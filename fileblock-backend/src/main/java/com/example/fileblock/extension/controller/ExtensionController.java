package com.example.fileblock.extension.controller;

import com.example.fileblock.extension.domain.CustomExtension;
import com.example.fileblock.extension.domain.FixedExtension;
import com.example.fileblock.extension.service.ExtensionService;
import com.example.fileblock.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extensions")
@RequiredArgsConstructor
public class ExtensionController {

    private final ExtensionService extensionService;

    // ===== 고정 확장자 =====

    @GetMapping("/fixed")
    public ApiResponse<List<FixedExtension>> getFixedExtensions() {
        return ApiResponse.success(extensionService.getFixedExtensions());
    }

    @PutMapping("/fixed/{seq}")
    public ApiResponse<FixedExtension> updateFixedExtension(
            @PathVariable Long seq,
            @RequestParam boolean isBlocked
    ) {
        return ApiResponse.success(extensionService.updateFixedExtension(seq, isBlocked));
    }

    // ===== 커스텀 확장자 =====

    @GetMapping("/custom")
    public ApiResponse<List<CustomExtension>> getCustomExtensions() {
        return ApiResponse.success(extensionService.getCustomExtensions());
    }

    @PostMapping("/custom")
    public ApiResponse<CustomExtension> addCustomExtension(@RequestParam String extension) {
        return ApiResponse.success(extensionService.addCustomExtension(extension));
    }

    @DeleteMapping("/custom/{seq}")
    public ApiResponse<?> removeCustomExtension(@PathVariable Long seq) {
        extensionService.removeCustomExtension(seq);
        return ApiResponse.success("deleted");
    }
}
