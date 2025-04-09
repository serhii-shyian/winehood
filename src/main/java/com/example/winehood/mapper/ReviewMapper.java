package com.example.winehood.mapper;

import com.example.winehood.config.MapperConfig;
import com.example.winehood.dto.review.CreateReviewRequestDto;
import com.example.winehood.dto.review.ReviewDto;
import com.example.winehood.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ReviewMapper {
    Review toEntity(CreateReviewRequestDto requestDto);

    @Mapping(source = "wine.id", target = "wineId")
    @Mapping(source = "user.id", target = "userId")
    ReviewDto toDto(Review review);
}
