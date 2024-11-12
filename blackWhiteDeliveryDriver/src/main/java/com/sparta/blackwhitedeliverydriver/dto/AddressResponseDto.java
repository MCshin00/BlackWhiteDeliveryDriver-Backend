package com.sparta.blackwhitedeliverydriver.dto;

import com.sparta.blackwhitedeliverydriver.entity.Address;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDto {
    private UUID id;

    private String zipNum;

    private String city;

    private String district;

    private String streetName;

    private String streetNum;

    private String detailAddr;

    public AddressResponseDto(Address address) {
        this.zipNum = address.getZipNum();
        this.city = address.getCity();
        this.district = address.getDistrict();
        this.streetName = address.getStreetName();
        this.streetNum = address.getStreetNum();
        this.detailAddr = address.getDetailAddr();
    }
}
