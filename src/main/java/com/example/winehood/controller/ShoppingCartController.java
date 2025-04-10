package com.example.winehood.controller;

import com.example.winehood.dto.cartitem.CartItemDto;
import com.example.winehood.dto.cartitem.CreateCartItemRequestDto;
import com.example.winehood.dto.cartitem.UpdateCartItemRequestDto;
import com.example.winehood.dto.shoppingcart.ShoppingCartDto;
import com.example.winehood.model.User;
import com.example.winehood.service.shoppingcart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
@Tag(name = "ShoppingCart management", description = "Endpoint for managing shopping cart")
@Validated
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a user shopping cart",
            description = "Getting a user shopping cart if available")
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto getUserShoppingCart(@AuthenticationPrincipal User user,
                                               @ParameterObject
                                               @PageableDefault(
                                                       size = 5,
                                                       sort = "wine",
                                                       direction = Sort.Direction.ASC)
                                               Pageable pageable) {
        return shoppingCartService.findShoppingCart(user, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a wine to shopping cart",
            description = "Adding a wine to shopping cart according to the parameters")
    @PreAuthorize("hasRole('USER')")
    public CartItemDto addWineToShoppingCart(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CreateCartItemRequestDto createCartDto) {
        return shoppingCartService.addWineToShoppingCart(user, createCartDto);
    }

    @PutMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update a wine in shopping cart",
            description = "Updating a wine in shopping cart according to the parameters")
    @PreAuthorize("hasRole('USER')")
    public CartItemDto updateWineInShoppingCart(
            @AuthenticationPrincipal User user,
            @PathVariable @Positive Long cartItemId,
            @RequestBody @Valid UpdateCartItemRequestDto requestDto) {
        return shoppingCartService.updateWineInShoppingCart(
                user, cartItemId, requestDto);
    }

    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a wine from shopping cart",
            description = "Delete a wine from shopping cart if available")
    @PreAuthorize("hasRole('USER')")
    public void deleteWineFromShoppingCart(@AuthenticationPrincipal User user,
                                           @PathVariable @Valid Long cartItemId) {
        shoppingCartService.deleteWineFromShoppingCart(user, cartItemId);
    }
}
