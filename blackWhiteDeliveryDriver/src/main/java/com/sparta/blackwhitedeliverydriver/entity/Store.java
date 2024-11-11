package com.sparta.blackwhitedeliverydriver.entity;

import com.sparta.blackwhitedeliverydriver.dto.StoreRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "p_stores")
public class Store extends StoreTimestamped {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "store_id", updatable = false, nullable = false)
    private UUID storeId;

    @Column(nullable = false, unique = true)
    private String storeName;

    private String phoneNumber;
    private Boolean status = true; // 매장 운영 여부
    private LocalTime openTime;
    private LocalTime closeTime;
    private Integer rating = 0; // 매장 평점 합산
    private Integer reviewCnt = 0;
    private String imgUrl;
    private String zipNum;
    private String city;
    private String district;
    private String streetName;
    private String streetNumber;
    private String detailAddr;
    private String storeIntro;
    private Boolean isPublic = false; // 매장 승인 여부

    public Store(String storeName,
                 String phoneNumber,
                 LocalTime openTime,
                 LocalTime closeTime,
                 String imgUrl,
                 String zipNum,
                 String city,
                 String district,
                 String streetName,
                 String streetNumber,
                 String detailAddr,
                 String storeIntro) {
        this.storeName = storeName;
        this.phoneNumber = phoneNumber;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.imgUrl = imgUrl;
        this.zipNum = zipNum;
        this.city = city;
        this.district = district;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.detailAddr = detailAddr;
        this.storeIntro = storeIntro;
        this.isPublic = true;
        this.rating = 0;
        this.reviewCnt = 0;
    }
}
