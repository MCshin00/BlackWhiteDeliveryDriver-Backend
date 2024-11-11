package com.sparta.blackwhitedeliverydriver.dto;

import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreRequestDto {
    private String storeName;
    private String phoneNumber;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String zipNum;
    private String city;
    private String district;
    private String streetName;
    private String streetNumber;
    private String detailAddr;
    private String storeIntro;
}
