package com.example.winehood.service.shoppingcart;

import com.example.winehood.dto.cartitem.CartItemDto;
import com.example.winehood.dto.cartitem.CreateCartItemRequestDto;
import com.example.winehood.dto.cartitem.UpdateCartItemRequestDto;
import com.example.winehood.dto.shoppingcart.ShoppingCartDto;
import com.example.winehood.exception.EntityNotFoundException;
import com.example.winehood.mapper.CartItemMapper;
import com.example.winehood.mapper.ShoppingCartMapper;
import com.example.winehood.model.CartItem;
import com.example.winehood.model.ShoppingCart;
import com.example.winehood.model.User;
import com.example.winehood.model.Wine;
import com.example.winehood.repository.cartitem.CartItemRepository;
import com.example.winehood.repository.shoppingcart.ShoppingCartRepository;
import com.example.winehood.repository.wine.WineRepository;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final WineRepository wineRepository;

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto findShoppingCart(User user, Pageable pageable) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(user.getId());
        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toDto(shoppingCart);
        shoppingCartDto.setCartItems(getCartItemsByShoppingCartId(shoppingCart.getId(), pageable));
        return shoppingCartDto;
    }

    @Override
    public CartItemDto addWineToShoppingCart(
            User user, CreateCartItemRequestDto createCartItemDto) {
        Wine wine = getWineById(createCartItemDto.wineId());
        ShoppingCart shoppingCart = getShoppingCartByUserId(user.getId());

        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(item -> item.getWine().getId().equals(wine.getId()))
                .findFirst()
                .orElseGet(() -> createNewCartItem(createCartItemDto, shoppingCart, wine));

        cartItem.setQuantity(createCartItemDto.quantity());
        cartItemRepository.save(cartItem);

        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public CartItemDto updateWineInShoppingCart(
            User user, Long cartItemId, UpdateCartItemRequestDto updateCartItemDto) {
        CartItem cartItem = validateCartItemBelongsToUser(cartItemId, user.getId());
        cartItem.setQuantity(updateCartItemDto.quantity());
        cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public void deleteWineFromShoppingCart(User user, Long cartItemId) {
        CartItem cartItem = validateCartItemBelongsToUser(cartItemId, user.getId());
        cartItemRepository.delete(cartItem);
    }

    private ShoppingCart getShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find ShoppingCart for userId: " + userId));
    }

    private Wine getWineById(Long wineId) {
        return wineRepository.findById(wineId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find Wine with wineId: " + wineId));
    }

    private CartItem validateCartItemBelongsToUser(Long cartItemId, Long userId) {
        CartItem cartItem = getCartItemById(cartItemId);
        ShoppingCart shoppingCart = cartItem.getShoppingCart();

        if (!shoppingCart.getUser().getId().equals(userId)) {
            throw new EntityNotFoundException(
                    "CartItem does not belong to this user's ShoppingCart");
        }

        return cartItem;
    }

    private CartItem getCartItemById(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find CartItem with cartItemId: " + cartItemId));
    }

    private CartItem createNewCartItem(
            CreateCartItemRequestDto createCartItemDto, ShoppingCart shoppingCart, Wine wine) {
        return cartItemMapper.toEntity(createCartItemDto)
                .setShoppingCart(shoppingCart)
                .setWine(wine);
    }

    private Set<CartItemDto> getCartItemsByShoppingCartId(Long shoppingCartId, Pageable pageable) {
        return cartItemRepository.findListByShoppingCartId(shoppingCartId, pageable).stream()
                .map(cartItemMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
