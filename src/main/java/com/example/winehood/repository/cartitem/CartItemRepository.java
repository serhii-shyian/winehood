package com.example.winehood.repository.cartitem;

import com.example.winehood.model.CartItem;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CartItemRepository extends JpaRepository<CartItem, Long>,
        JpaSpecificationExecutor<CartItem> {
    List<CartItem> findListByShoppingCartId(Long shoppingCartId, Pageable pageable);
}
