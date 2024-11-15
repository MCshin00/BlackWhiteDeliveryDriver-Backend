package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.StoreRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.Store;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.repository.StoreRepository;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    @Transactional
    public UUID createStore(@Valid StoreRequestDto requestDto, User user) {
        // 점포 중복확인 (이름, 전화번호)

        // 점포 등록
        storeRepository.save(Store.from(requestDto, user));

        // 점포 조회
        Optional<Store> res = Optional.ofNullable(
                storeRepository.findByStoreNameAndPhoneNumber(requestDto.getStoreName(), requestDto.getPhoneNumber())
                        .orElseThrow(
                                () -> new IllegalArgumentException("점포를 찾을 수 없습니다.")
                        ));

        return res.get().getStoreId();
    }

    @Transactional
    public UUID updateStore(UUID storeId, StoreRequestDto requestDto, UserDetailsImpl userDetails) {
        // 점포 조회
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new NullPointerException(requestDto.getStoreName() + "은(는) 존재하지 않는 점포입니다.")
        );

        store.update(requestDto, userDetails);
        store.setLastModifiedDate(LocalDateTime.now());
        store.setLastModifiedBy(userDetails.getUsername());

        return store.getStoreId();
    }

    public List<StoreResponseDto> getStores() {
        return storeRepository.findAll().stream().map(StoreResponseDto::new).toList();
    }

    public StoreResponseDto getStore(UUID storeId) {
        return storeRepository.findById(storeId).map(StoreResponseDto::new).orElseThrow(
                () -> new NullPointerException("해당 점포를 찾을 수 없습니다.")
        );
    }

    @Transactional
    public void deleteStore(UUID storeId, UserDetailsImpl userDetails) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new NullPointerException("존재하지않는 점포입니다.")
        );

        store.setDeletedDate(LocalDateTime.now());
        store.setDeletedBy(userDetails.getUsername());
    }

    public List<StoreResponseDto> getStoreOfOwner(User user) {
        List<Store> storeList = storeRepository.findAllByUser(user);
        List<StoreResponseDto> responseDtoList = new ArrayList<>();

        for (Store store : storeList) {
            responseDtoList.add(new StoreResponseDto(store));
        }

        return responseDtoList;
    }

    public String getNameOfOwner(UUID storeId) {
        User user = storeRepository.findById(storeId).map(Store::getUser).orElseThrow(
                () -> new IllegalArgumentException(storeId + "라는 점포가 없습니다.")
        );

        return user.getUsername();
    }
}
