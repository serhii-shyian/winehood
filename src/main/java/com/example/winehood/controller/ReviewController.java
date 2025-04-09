package com.example.winehood.controller;

import com.example.winehood.dto.review.CreateReviewRequestDto;
import com.example.winehood.dto.review.ReviewDto;
import com.example.winehood.model.User;
import com.example.winehood.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/reviews")
@Tag(name = "Review management", description = "Endpoint for managing reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new review",
            description = "Creating a new review according to the parameters")
    @PreAuthorize("hasRole('USER')")
    public ReviewDto createReview(Authentication authentication,
                                  @RequestBody @Valid CreateReviewRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return reviewService.save(user, requestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all reviews",
            description = "Getting all reviews by wine id")
    @PreAuthorize("hasRole('USER')")
    public Page<ReviewDto> getAllReviewsByWineId(@RequestParam(name = "wineId") Long wineId,
                                                 @ParameterObject @PageableDefault(
                                                         size = 5,
                                                         sort = "rating",
                                                         direction = Sort.Direction.DESC)
                                                 Pageable pageable) {
        return reviewService.findAllReviewsByWineId(wineId, pageable);
    }
}
