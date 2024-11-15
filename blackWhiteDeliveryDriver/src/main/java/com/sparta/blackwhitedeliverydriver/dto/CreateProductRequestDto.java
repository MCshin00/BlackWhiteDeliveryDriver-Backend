package com.sparta.blackwhitedeliverydriver.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Getter;

@Getter
public class CreateProductRequestDto {
    @NotBlank
    private UUID storeId;
    @NotBlank
    private String name;
    @NotBlank
    private Integer price;
    private String imgUrl;
    @NotBlank
    private String productIntro;
}
