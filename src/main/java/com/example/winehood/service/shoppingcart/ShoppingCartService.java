package com.example.winehood.service.shoppingcart;

import com.example.winehood.dto.cartitem.CartItemDto;
import com.example.winehood.dto.cartitem.CreateCartItemRequestDto;
import com.example.winehood.dto.cartitem.UpdateCartItemRequestDto;
import com.example.winehood.dto.shoppingcart.ShoppingCartDto;
import com.example.winehood.model.User;
import org.springframework.data.domain.Pageable;

public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartDto findShoppingCart(
            User user,
            Pageable pageable);

    CartItemDto addWineToShoppingCart(
            User user,
            CreateCartItemRequestDto createCartDto);

    CartItemDto updateWineInShoppingCart(
            User user,
            Long cartItemId,
            UpdateCartItemRequestDto updateCartDto);

    void deleteWineFromShoppingCart(
            User user,
            Long cartItemId);
}
