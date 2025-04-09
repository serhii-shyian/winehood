package com.example.winehood.service.review;

import com.example.winehood.dto.review.CreateReviewRequestDto;
import com.example.winehood.dto.review.ReviewDto;
import com.example.winehood.exception.EntityNotFoundException;
import com.example.winehood.mapper.ReviewMapper;
import com.example.winehood.model.Review;
import com.example.winehood.model.User;
import com.example.winehood.model.Wine;
import com.example.winehood.repository.review.ReviewRepository;
import com.example.winehood.repository.wine.WineRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final WineRepository wineRepository;

    @Override
    public ReviewDto save(User user, CreateReviewRequestDto requestDto) {
        Wine wineFromDb = wineRepository.findById(requestDto.wineId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find wine by id: " + requestDto.wineId()));
        Review reviewFromDto = reviewMapper.toEntity(requestDto);
        reviewFromDto.setWine(wineFromDb);
        reviewFromDto.setUser(user);
        reviewFromDto.setTimestamp(LocalDateTime.now());
        return reviewMapper.toDto(reviewRepository.save(reviewFromDto));
    }

    @Override
    public Page<ReviewDto> findAllReviewsByWineId(Long wineId, Pageable pageable) {
        return reviewRepository.findAllByWineId(wineId, pageable)
                .map(reviewMapper::toDto);
    }
}
