package com.example.winehood.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CartItem {
    @Id
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_cart_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ShoppingCart shoppingCart;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Wine wine;
    @Column(nullable = false)
    private Integer quantity;
}
