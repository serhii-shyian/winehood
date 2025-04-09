package com.example.winehood.mapper;

import com.example.winehood.config.MapperConfig;
import com.example.winehood.dto.orderitem.OrderItemDto;
import com.example.winehood.model.CartItem;
import com.example.winehood.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(source = "wine.id", target = "wineId")
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(source = "wine.price", target = "price")
    @Mapping(target = "id", ignore = true)
    OrderItem toEntityFromCartItem(CartItem cartItem);
}
