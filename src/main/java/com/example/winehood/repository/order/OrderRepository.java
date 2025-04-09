package com.example.winehood.repository.order;

import com.example.winehood.model.Order;
import com.example.winehood.model.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Region> {
    Page<Order> findAllByUserId(Long userId, Pageable pageable);
}
