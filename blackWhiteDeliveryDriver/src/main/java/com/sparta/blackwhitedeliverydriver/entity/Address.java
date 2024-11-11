package com.sparta.blackwhitedeliverydriver.entity;

import com.sparta.blackwhitedeliverydriver.dto.AddressRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "p_address")
public class Address extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_username")
    private User user;

    @Column(nullable = false)
    private String zipNum;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String streetName;

    @Column(nullable = false)
    private String streetNum;

    @Column(nullable = false)
    private String detailAddr;

    public Address(AddressRequestDto requestDto, User user) {
        this.user = user;
        this.zipNum = requestDto.getZipNum();
        this.city = requestDto.getCity();
        this.district = requestDto.getDistrict();
        this.streetName = requestDto.getStreetName();
        this.streetNum = requestDto.getStreetNum();
        this.detailAddr = requestDto.getDetailAddr();
    }
}
