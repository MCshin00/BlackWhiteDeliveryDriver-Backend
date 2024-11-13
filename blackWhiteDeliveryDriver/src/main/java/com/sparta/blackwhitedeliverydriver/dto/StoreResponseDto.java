package com.sparta.blackwhitedeliverydriver.dto;

import com.sparta.blackwhitedeliverydriver.entity.Store;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreResponseDto {
    private UUID storeId;
    private String storeName;
    private String phoneNumber;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String imgUrl;
    private String zipNum;
    private String city;
    private String district;
    private String streetName;
    private String streetNumber;
    private String detailAddr;
    private String storeIntro;

    public StoreResponseDto(Store store){
        this.storeId = store.getStoreId();
        this.storeName = store.getStoreName();
        this.phoneNumber = store.getPhoneNumber();
        this.openTime = store.getOpenTime();
        this.closeTime = store.getCloseTime();
        this.imgUrl = store.getImgUrl();
        this.zipNum = store.getZipNum();
        this.city = store.getCity();
        this.district = store.getDistrict();
        this.streetName = store.getStreetName();
        this.streetNumber = store.getStreetNumber();
        this.detailAddr = store.getDetailAddr();
        this.storeIntro = store.getStoreIntro();
    }
}
