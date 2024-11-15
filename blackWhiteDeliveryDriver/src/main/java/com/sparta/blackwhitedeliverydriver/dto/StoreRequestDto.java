package com.sparta.blackwhitedeliverydriver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreRequestDto {
    @NotBlank
    private String storeName;
    @NotBlank
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호는 010-0000-0000 형식이어야 합니다.")
    private String phoneNumber;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String imgUrl;
    @NotBlank
    private String zipNum;
    @NotBlank
    private String city;
    @NotBlank
    private String district;
    @NotBlank
    private String streetName;
    @NotBlank
    private String streetNumber;
    @NotBlank
    private String detailAddr;
    @NotBlank
    private String storeIntro;
}
