package com.sparta.blackwhitedeliverydriver.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_order")
public class Order extends BaseEntity {
    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer finalPay;

    @Column(nullable = false)
    private Integer discountRate;

    @Column(nullable = false)
    private Integer discountAmount;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OrderStatusEnum status;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

    public static Order ofUserAndStore(User user, Store store) {
        return Order.builder()
                .user(user)
                .store(store)
                .finalPay(0)
                .discountAmount(0)
                .discountRate(0)
                .status(OrderStatusEnum.CREATE)
                .build();
    }

    public void updateFinalPay(int price) {
        this.finalPay = price;
    }

    public void updateStatus(OrderStatusEnum status) {
        this.status = status;
    }
}
