package com.example.fileblock.extension.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "custom_extension")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomExtension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(nullable = false, unique = true, length = 20)
    private String extension;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public static CustomExtension of(String extension) {
        return CustomExtension.builder()
                .extension(extension.toLowerCase())
                .build();
    }
}

