package com.example.fileblock.global.exception;

import com.example.fileblock.global.response.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ApiResponse<?> handleNotFound(NotFoundException e) {
        return ApiResponse.error("NotFound", e.getMessage());
    }

    @ExceptionHandler(DuplicateExtensionException.class)
    public ApiResponse<?> handleDuplicate(DuplicateExtensionException e) {
        return ApiResponse.error("DuplicateExtension", e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ApiResponse<?> handleValidation(ValidationException e) {
        return ApiResponse.error("ValidationError", e.getMessage());
    }

    @ExceptionHandler(LimitExceededException.class)
    public ApiResponse<?> handleLimitExceeded(LimitExceededException e) {
        return ApiResponse.error("LimitExceeded", e.getMessage());
    }

    @ExceptionHandler(BlockedExtensionException.class)
    public ApiResponse<?> handleBlocked(BlockedExtensionException e) {
        return ApiResponse.error("BlockedExtension", e.getMessage());
    }

    @ExceptionHandler(InvalidFileTypeException.class)
    public ApiResponse<?> handleInvalidFileType(InvalidFileTypeException e) {
        return ApiResponse.error("InvalidFileType", e.getMessage());
    }

    // fallback - 예상 못한 예외
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleGeneric(Exception e) {
        return ApiResponse.error("InternalServerError", e.getMessage());
    }
}
