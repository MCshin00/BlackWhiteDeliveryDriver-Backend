package com.sparta.blackwhitedeliverydriver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotBlank
    @Pattern(
            regexp = "^(?:[01]\\d|2[0-3]):[0-5][0]$",
            message = "시간은 HH:mm 형식의 10분 단위로 입력해야 합니다 (예: 08:10, 13:40, 23:50)."
    )
    private LocalTime openTime;
    @NotBlank
    @Pattern(
            regexp = "^(?:[01]\\d|2[0-3]):[0-5][0]$",
            message = "시간은 HH:mm 형식의 10분 단위로 입력해야 합니다 (예: 08:10, 13:40, 23:50)."
    )
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
