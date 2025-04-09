package com.example.winehood.dto.shoppingcart;

import com.example.winehood.dto.cartitem.CartItemDto;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemDto> cartItems;
}
