package com.example.winehood.repository.shoppingcart;

import com.example.winehood.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long>,
        JpaSpecificationExecutor<ShoppingCart> {
    Optional<ShoppingCart> findByUserId(Long userId);
}
