package com.example.winehood.mapper;

import com.example.winehood.config.MapperConfig;
import com.example.winehood.dto.shoppingcart.ShoppingCartDto;
import com.example.winehood.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "cartItems", ignore = true)
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
