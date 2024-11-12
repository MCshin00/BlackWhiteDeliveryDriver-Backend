package com.sparta.blackwhitedeliverydriver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "p_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String user; // 유저 외래키 연동 필요
    @Column(nullable = false)
    private Integer finalPay;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer discountRate;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer discountAmount;
}
