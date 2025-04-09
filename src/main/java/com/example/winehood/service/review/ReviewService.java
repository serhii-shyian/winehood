package com.example.winehood.service.review;

import com.example.winehood.dto.review.CreateReviewRequestDto;
import com.example.winehood.dto.review.ReviewDto;
import com.example.winehood.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewDto save(User user, CreateReviewRequestDto requestDto);

    Page<ReviewDto> findAllReviewsByWineId(Long wineId, Pageable pageable);
}
