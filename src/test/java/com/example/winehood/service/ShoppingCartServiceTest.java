package com.example.winehood.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.winehood.dto.cartitem.CartItemDto;
import com.example.winehood.dto.cartitem.CreateCartItemRequestDto;
import com.example.winehood.dto.cartitem.UpdateCartItemRequestDto;
import com.example.winehood.dto.shoppingcart.ShoppingCartDto;
import com.example.winehood.exception.EntityNotFoundException;
import com.example.winehood.mapper.CartItemMapper;
import com.example.winehood.mapper.ShoppingCartMapper;
import com.example.winehood.model.CartItem;
import com.example.winehood.model.Region;
import com.example.winehood.model.ShoppingCart;
import com.example.winehood.model.User;
import com.example.winehood.model.Wine;
import com.example.winehood.repository.cartitem.CartItemRepository;
import com.example.winehood.repository.shoppingcart.ShoppingCartRepository;
import com.example.winehood.repository.wine.WineRepository;
import com.example.winehood.service.shoppingcart.ShoppingCartServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private WineRepository wineRepository;

    @Test
    @DisplayName("""
            Find ShoppingCart when given an existing user
            """)
    void findShoppingCart_ExistingUser_ReturnsShoppingCartDto() {
        //Given
        User user = getTestUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        Wine wine = getWine();
        CartItem cartItem = getCartItem(shoppingCart, wine);
        CartItemDto cartItemDto = getCartItemDto(cartItem);
        Pageable pageable = PageRequest.of(0, 5);
        ShoppingCartDto expected = new ShoppingCartDto()
                .setId(shoppingCart.getId())
                .setUserId(shoppingCart.getUser().getId())
                .setCartItems(Set.of(cartItemDto));

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        when(cartItemRepository.findListByShoppingCartId(expected.getId(), pageable))
                .thenReturn(List.of(cartItem));
        when(cartItemMapper.toDto(cartItem)).thenReturn(cartItemDto);

        //When
        ShoppingCartDto actual = shoppingCartService.findShoppingCart(user, pageable);

        //Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1))
                .findByUserId(user.getId());
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verify(cartItemRepository, times(1))
                .findListByShoppingCartId(expected.getId(), pageable);
        verify(cartItemMapper, times(1)).toDto(cartItem);
    }

    @Test
    @DisplayName("""
            Find ShoppingCart when given non existing user
            """)
    void findShoppingCart_NonExistingUser_ThrowsException() {
        //Given
        User user = getTestUser();

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.empty());

        //Then
        Pageable pageable = PageRequest.of(0, 5);
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.findShoppingCart(user, pageable));
        verify(shoppingCartRepository, times(1))
                .findByUserId(user.getId());
    }

    @Test
    @DisplayName("""
            Add wine to ShoppingCart when given valid wine
            """)
    void addWineToShoppingCart_ValidWine_ReturnsCartItemDto() {
        //Given
        User user = getTestUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        Wine wine = getWine();
        CreateCartItemRequestDto requestDto = getCartItemRequestDto(wine);
        CartItem cartItem = getCartItem(shoppingCart, wine);
        CartItemDto expected = getCartItemDto(cartItem);

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(wineRepository.findById(requestDto.wineId()))
                .thenReturn(Optional.of(wine));
        when(cartItemMapper.toEntity(requestDto)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(expected);

        //When
        CartItemDto actual = shoppingCartService
                .addWineToShoppingCart(user, requestDto);

        //Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1)).findByUserId(user.getId());
        verify(wineRepository, times(1)).findById(requestDto.wineId());
        verify(cartItemMapper, times(1)).toEntity(requestDto);
        verify(cartItemMapper, times(1)).toDto(cartItem);
    }

    @Test
    @DisplayName("""
            Add wine to ShoppingCart when wine does not exist
            """)
    void addWineToShoppingCart_NonExistingWine_ThrowsException() {
        //Given
        User user = new User();
        Wine wine = getWine();
        CreateCartItemRequestDto requestDto = getCartItemRequestDto(wine.setId(99L));

        when(wineRepository.findById(requestDto.wineId()))
                .thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class, () -> shoppingCartService
                .addWineToShoppingCart(user, requestDto));
        verify(wineRepository, times(1)).findById(requestDto.wineId());
    }

    @Test
    @DisplayName("""
            Update wine in ShoppingCart when given valid input data
            """)
    void updateWineInShoppingCart_ValidData_ReturnsCartItemDto() {
        //Given
        User user = getTestUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        Wine wine = getWine();
        CartItem cartItem = getCartItem(shoppingCart, wine);
        UpdateCartItemRequestDto updateRequestDto = new UpdateCartItemRequestDto(10);
        CartItemDto expected = new CartItemDto(
                cartItem.getId(),
                cartItem.getWine().getId(),
                cartItem.getWine().getName(),
                10);

        when(cartItemRepository.findById(cartItem.getId()))
                .thenReturn(Optional.of(cartItem));
        when(cartItemMapper.toDto(cartItem)).thenReturn(expected);

        //When
        CartItemDto actual = shoppingCartService
                .updateWineInShoppingCart(user, cartItem.getId(), updateRequestDto);

        //Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(cartItemRepository, times(1)).findById(cartItem.getId());
        verify(cartItemMapper, times(1)).toDto(cartItem);
    }

    @Test
    @DisplayName("""
            Update wine in ShoppingCart when cart item does not exist
            """)
    void updateWineInShoppingCart_NonExistingCartItem_ThrowsException() {
        //Given
        User user = getTestUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        Wine wine = getWine();
        CartItem cartItem = getCartItem(shoppingCart, wine);
        UpdateCartItemRequestDto updateRequestDto = new UpdateCartItemRequestDto(10);

        when(cartItemRepository.findById(cartItem.getId()))
                .thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class, () -> shoppingCartService
                .updateWineInShoppingCart(user, cartItem.getId(), updateRequestDto));
        verify(cartItemRepository, times(1)).findById(cartItem.getId());
    }

    @Test
    @DisplayName("""
            Delete wine from ShoppingCart when CartItem Id exist
            """)
    void deleteWineFromShoppingCart_ExistingId_ReturnsNothing() {
        //Given
        User user = getTestUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        Wine wine = getWine();
        CartItem cartItem = getCartItem(shoppingCart, wine);

        when(cartItemRepository.findById(cartItem.getId()))
                .thenReturn(Optional.of(cartItem));

        //When
        shoppingCartService.deleteWineFromShoppingCart(user, cartItem.getId());

        //Then
        verify(cartItemRepository, times(1)).findById(cartItem.getId());
        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    @DisplayName("""
            Delete wine from ShoppingCart when CartItem Id does not exist
            """)
    void deleteWineFromShoppingCart_NonExistingId_ThrowsException() {
        //Given
        User user = getTestUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        Wine wine = getWine();
        CartItem cartItem = getCartItem(shoppingCart, wine);

        when(cartItemRepository.findById(cartItem.getId()))
                .thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class, () -> {
            shoppingCartService.deleteWineFromShoppingCart(user, cartItem.getId());
        });
        verify(cartItemRepository, times(1)).findById(cartItem.getId());
    }

    private User getTestUser() {
        return new User()
                .setId(4L)
                .setUsername("john.doe")
                .setEmail("john.doe@example.com")
                .setPassword("password123")
                .setFirstName("John")
                .setLastName("Doe")
                .setShippingAddress("Ukraine");
    }

    private ShoppingCart getShoppingCart(User user) {
        return new ShoppingCart()
                .setId(user.getId())
                .setUser(user);
    }

    private Wine getWine() {
        return new Wine()
                .setId(1L)
                .setName("Wine A")
                .setPrice(BigDecimal.valueOf(19.99))
                .setGrapeVariety("Merlot")
                .setRegion(getTestRegion());
    }

    private Region getTestRegion() {
        return new Region()
                .setId(1L)
                .setName("Napa Valley")
                .setCountry("USA");
    }

    private CartItem getCartItem(ShoppingCart shoppingCart, Wine wine) {
        return new CartItem()
                .setId(1L)
                .setShoppingCart(shoppingCart)
                .setWine(wine)
                .setQuantity(6);
    }

    private CartItemDto getCartItemDto(CartItem cartItem) {
        return new CartItemDto(
                cartItem.getId(),
                cartItem.getWine().getId(),
                cartItem.getWine().getName(),
                cartItem.getQuantity());
    }

    private CreateCartItemRequestDto getCartItemRequestDto(Wine wine) {
        return new CreateCartItemRequestDto(
                wine.getId(),
                1);
    }
}
