package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.StoreRequestDto;
import com.sparta.blackwhitedeliverydriver.entity.Store;
import com.sparta.blackwhitedeliverydriver.repository.StoreRepository;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.RequestContextFilter;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final RequestContextFilter requestContextFilter;

    public UUID save(@Valid StoreRequestDto requestDto) {
        // 점포 중복확인 (이름, 전화번호)

        // 점포 등록
        Store store = new Store(
                requestDto.getStoreName(),
                requestDto.getPhoneNumber(),
                requestDto.getOpenTime(),
                requestDto.getCloseTime(),
                "imgUrl",
                requestDto.getZipNum(),
                requestDto.getCity(),
                requestDto.getDistrict(),
                requestDto.getStreetName(),
                requestDto.getStreetNumber(),
                requestDto.getDetailAddr(),
                requestDto.getStoreIntro()
                );
        storeRepository.save(store);

        // 점포 조회
        Optional<Store> res = storeRepository.findByStoreNameAndPhoneNumber(requestDto.getStoreName(), requestDto.getPhoneNumber());

        return res.get().getStoreId();
    }
}
