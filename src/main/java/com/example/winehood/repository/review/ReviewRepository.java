package com.example.winehood.repository.review;

import com.example.winehood.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByWineId(Long wineId, Pageable pageable);
}
