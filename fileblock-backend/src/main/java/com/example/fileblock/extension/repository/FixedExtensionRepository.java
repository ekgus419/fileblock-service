package com.example.fileblock.extension.repository;

import com.example.fileblock.extension.domain.FixedExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FixedExtensionRepository extends JpaRepository<FixedExtension, Long> {

    Optional<FixedExtension> findByExtension(String extension);

    boolean existsByExtension(String extension);
}
