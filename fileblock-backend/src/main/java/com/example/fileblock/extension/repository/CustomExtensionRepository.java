package com.example.fileblock.extension.repository;

import com.example.fileblock.extension.domain.CustomExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomExtensionRepository extends JpaRepository<CustomExtension, Long> {

    Optional<CustomExtension> findByExtension(String extension);

    boolean existsByExtension(String extension);

    long count(); // 최대 200개 제한 체크용
}
