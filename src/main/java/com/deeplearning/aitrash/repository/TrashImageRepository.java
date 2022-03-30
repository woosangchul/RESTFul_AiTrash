package com.deeplearning.aitrash.repository;

import com.deeplearning.aitrash.entity.TrashImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrashImageRepository extends JpaRepository<TrashImage, Long> {
}
