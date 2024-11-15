package com.sparta.blackwhitedeliverydriver.dto;

import com.sparta.blackwhitedeliverydriver.entity.Store;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreIdResponseDto {
    private UUID storeId;

    public StoreIdResponseDto(UUID storeId){
        this.storeId = storeId;
    }
}
