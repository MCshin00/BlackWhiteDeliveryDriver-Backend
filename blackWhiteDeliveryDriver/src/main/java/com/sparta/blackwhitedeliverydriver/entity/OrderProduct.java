package com.sparta.blackwhitedeliverydriver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "p_order_product")
public class OrderProduct extends BaseEntity{
    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer price;

    public static OrderProduct ofBasketAndOrder(Basket basket, Order order){
        return OrderProduct.builder()
                .product(basket.getProductId()) //product가 없기 때문에 임시값 대입
                .order(order)
                .quantity(basket.getQuantity())
                .price(5000) //product가 없기 때문에 임시값 대입
                .build();
    }

}
