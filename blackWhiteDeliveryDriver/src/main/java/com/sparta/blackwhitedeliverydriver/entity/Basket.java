package com.sparta.blackwhitedeliverydriver.entity;

import com.sparta.blackwhitedeliverydriver.dto.BasketAddRequestDto;
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
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_basket")
public class Basket extends BaseEntity {
    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    private UUID productId; // 임시 컬럼

    @Column(nullable = false)
    private Integer quantity;

    public static Basket ofUserAndOrderProduct(User user, OrderProduct orderProduct) {
        return Basket.builder()
                .productId(orderProduct.getProduct())
                .quantity(orderProduct.getQuantity())
                .user(user)
                .build();
    }

    public void updateBasketOfQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public static Basket ofUserAndRequest(User user, BasketAddRequestDto requestDto) {
        return Basket.builder()
                .productId(requestDto.getProductId())
                .quantity(requestDto.getQuantity())
                .user(user)
                .build();
    }

}
