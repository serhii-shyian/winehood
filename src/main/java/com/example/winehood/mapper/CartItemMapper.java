package com.example.winehood.mapper;

import com.example.winehood.config.MapperConfig;
import com.example.winehood.dto.cartitem.CartItemDto;
import com.example.winehood.dto.cartitem.CreateCartItemRequestDto;
import com.example.winehood.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    CartItem toEntity(CreateCartItemRequestDto cartItemDto);

    @Mapping(source = "wine.id", target = "wineId")
    @Mapping(source = "wine.name", target = "wineName")
    CartItemDto toDto(CartItem cartItem);
}
