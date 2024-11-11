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

    public Store(StoreRequestDto requestDto) {
        this.storeName = requestDto.getStoreName();
        this.phoneNumber = requestDto.getPhoneNumber();
        this.openTime = requestDto.getOpenTime();
        this.closeTime = requestDto.getCloseTime();
        this.imgUrl = requestDto.getImgUrl();
        this.zipNum = requestDto.getZipNum();
        this.city = requestDto.getCity();
        this.district = requestDto.getDistrict();
        this.streetName = requestDto.getStreetName();
        this.streetNumber = requestDto.getStreetNumber();
        this.detailAddr = requestDto.getDetailAddr();
        this.storeIntro = requestDto.getStoreIntro();
        this.isPublic = true;
        this.rating = 0;
        this.reviewCnt = 0;
    }

    public void update(StoreRequestDto requestDto) {
        this.storeName = requestDto.getStoreName();
        this.phoneNumber = requestDto.getPhoneNumber();
        this.openTime = requestDto.getOpenTime();
        this.closeTime = requestDto.getCloseTime();
        this.imgUrl = requestDto.getImgUrl();
        this.zipNum = requestDto.getZipNum();
        this.city = requestDto.getCity();
        this.district = requestDto.getDistrict();
        this.streetName = requestDto.getStreetName();
        this.streetNumber = requestDto.getStreetNumber();
        this.detailAddr = requestDto.getDetailAddr();
        this.storeIntro = requestDto.getStoreIntro();
    }
}
