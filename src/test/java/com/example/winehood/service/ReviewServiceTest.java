package com.example.winehood.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.winehood.dto.review.CreateReviewRequestDto;
import com.example.winehood.dto.review.ReviewDto;
import com.example.winehood.exception.EntityNotFoundException;
import com.example.winehood.mapper.ReviewMapper;
import com.example.winehood.model.Region;
import com.example.winehood.model.Review;
import com.example.winehood.model.User;
import com.example.winehood.model.Wine;
import com.example.winehood.repository.review.ReviewRepository;
import com.example.winehood.repository.wine.WineRepository;
import com.example.winehood.service.review.ReviewServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private WineRepository wineRepository;

    @Test
    @DisplayName("""
            Save a new review and return the corresponding review DTO
            """)
    void saveReview_ValidRequest_ReturnsReviewDto() {
        // Given
        User user = getUser();
        Wine wine = getWine();
        CreateReviewRequestDto requestDto = getCreateReviewRequestDto();
        Review review = getReview();
        ReviewDto expectedDto = getReviewDto();

        when(wineRepository.findById(requestDto.wineId())).thenReturn(Optional.of(wine));
        when(reviewMapper.toEntity(requestDto)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toDto(review)).thenReturn(expectedDto);

        // When
        ReviewDto actual = reviewService.save(user, requestDto);

        // Then
        assertEquals(expectedDto, actual);
        verify(wineRepository).findById(requestDto.wineId());
        verify(reviewMapper).toEntity(requestDto);
        verify(reviewRepository).save(review);
        verify(reviewMapper).toDto(review);
    }

    @Test
    @DisplayName("""
            Save a review when wine ID does not exist throws EntityNotFoundException
            """)
    void saveReview_NonExistentWine_ThrowsEntityNotFoundException() {
        // Given
        User user = getUser();
        CreateReviewRequestDto requestDto = getCreateReviewRequestDto();

        when(wineRepository.findById(requestDto.wineId()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> reviewService.save(user, requestDto));
        verify(wineRepository).findById(requestDto.wineId());
    }

    @Test
    @DisplayName("""
            Find all reviews by wine ID and return mapped page
            """)
    void findAllReviewsByWineId_ValidId_ReturnsReviewDtoPage() {
        // Given
        Review review = getReview();
        ReviewDto reviewDto = getReviewDto();
        Page<Review> reviewPage = new PageImpl<>(List.of(review));
        when(reviewRepository.findAllByWineId(
                1L, PageRequest.of(0, 5))).thenReturn(reviewPage);
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        // When
        Page<ReviewDto> actual = reviewService.findAllReviewsByWineId(
                1L, PageRequest.of(0, 5));

        // Then
        assertEquals(1, actual.getTotalElements());
        assertEquals(reviewDto, actual.getContent().getFirst());
        verify(reviewRepository).findAllByWineId(1L, PageRequest.of(0, 5));
        verify(reviewMapper).toDto(review);
    }

    @Test
    @DisplayName("""
            Find all reviews by wine ID returns empty page when no reviews exist
            """)
    void findAllReviewsByWineId_NoReviews_ReturnsEmptyPage() {
        // Given
        Page<Review> emptyPage = Page.empty();
        when(reviewRepository.findAllByWineId(
                99L, PageRequest.of(0, 5))).thenReturn(emptyPage);

        // When
        Page<ReviewDto> actual = reviewService.findAllReviewsByWineId(
                99L, PageRequest.of(0, 5));

        // Then
        assertEquals(0, actual.getTotalElements());
        verify(reviewRepository).findAllByWineId(99L, PageRequest.of(0, 5));
    }

    private User getUser() {
        return new User()
                .setId(1L)
                .setUsername("tester");
    }

    private Wine getWine() {
        Region region = new Region()
                .setId(1L)
                .setName("Napa Valley")
                .setCountry("USA");
        return new Wine()
                .setId(1L)
                .setName("Wine A")
                .setPrice(BigDecimal.valueOf(20.0))
                .setGrapeVariety("Merlot")
                .setRegion(region);
    }

    private CreateReviewRequestDto getCreateReviewRequestDto() {
        return new CreateReviewRequestDto(
                1L,
                "Great wine with smooth finish",
                4.5);
    }

    private Review getReview() {
        Review review = new Review();
        review.setId(1L);
        review.setText("Great wine with smooth finish");
        review.setRating(4.5);
        review.setWine(getWine());
        review.setUser(getUser());
        review.setTimestamp(LocalDateTime.now());
        return review;
    }

    private ReviewDto getReviewDto() {
        return new ReviewDto(
                1L,
                1L,
                1L,
                4.5,
                "Great wine with smooth finish",
                LocalDateTime.now());
    }
}
