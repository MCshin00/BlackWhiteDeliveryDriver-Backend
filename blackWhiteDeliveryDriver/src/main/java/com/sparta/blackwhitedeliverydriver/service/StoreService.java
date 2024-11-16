package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.StoreIdResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.Category;
import com.sparta.blackwhitedeliverydriver.entity.Store;
import com.sparta.blackwhitedeliverydriver.entity.StoreCategory;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.repository.CategoryRepository;
import com.sparta.blackwhitedeliverydriver.repository.StoreCategoryRepository;
import com.sparta.blackwhitedeliverydriver.repository.StoreRepository;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public UUID createStore(@Valid StoreRequestDto requestDto, List<Category> categoryList, User user) {
        // 점포 중복확인 (이름, 전화번호)
        checkStoreNameAndPhoneNumber(requestDto.getStoreName(), requestDto.getPhoneNumber());

        // 점포 등록
        Store store = Store.from(requestDto, user);
        storeRepository.save(store);
        for(Category category : categoryList) {
            StoreCategory storeCategory = StoreCategory.from(store, category);
            storeCategoryRepository.save(storeCategory);
        }
        return store.getStoreId();
    }

    @Transactional
    public UUID updateStore(UUID storeId, StoreRequestDto requestDto, List<Category> newCategoryList, UserDetailsImpl userDetails) {
        // 점포 조회
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new NullPointerException(requestDto.getStoreName() + "은(는) 존재하지 않는 점포입니다.")
        );

        // OWNER의 가게인지 확인 -> 본인 가게만 수정, MANAGER와 MASTER는 검증하지 않음
        if(userDetails.getUser().getRole().equals(UserRoleEnum.OWNER)) {
            checkStoreOfOwner(storeId, userDetails);
        }

        // 기존 카테고리 전체 삭제
        storeCategoryRepository.deleteAllByStoreStoreId(storeId);

        // 새로운 카테고리 추가
        Set<Category> categorySet = new HashSet<>();
        for(Category category : newCategoryList) {
            categorySet.add(category);
        }

        // 신규 카테고리는 저장
        List<String> categoryNameList = new ArrayList<>();
        for(Category category : categorySet) {
            categoryNameList.add(category.getName());
            StoreCategory storeCategory = storeCategoryRepository.findByStoreStoreIdAndCategoryCategoryId(store.getStoreId(), category.getCategoryId())
                            .orElseGet(() -> {
                               StoreCategory newStoreCategory = StoreCategory.from(store, category);
                               return storeCategoryRepository.save(newStoreCategory);
                            });
        }
        store.update(requestDto, userDetails);

        return store.getStoreId();
    }

    @Transactional
    public StoreIdResponseDto updateStorePublic(UUID storeId, boolean isPublic) {
        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new IllegalArgumentException("해당 점포를 찾을 수 없습니다."));

        store.updatePublicStore(isPublic);
        storeRepository.save(store);

        return new StoreIdResponseDto(store.getStoreId());
    }

    public List<StoreResponseDto> getStoreList() {
        List<Store> storeList = storeRepository.findAll();
        List<StoreResponseDto> storeResponseDtoList = new ArrayList<>();

        for (Store store : storeList) {
            List<StoreCategory> storeCategoryList = storeCategoryRepository.findAllByStoreStoreId(store.getStoreId());
            List<String> categoryNameList = new ArrayList<>();
            for (StoreCategory storeCategory : storeCategoryList){
                Optional<Category> category = Optional.ofNullable(categoryRepository.findById(storeCategory.getCategory().getCategoryId()).orElseThrow(
                        () -> new NullPointerException(storeCategory.getCategory().getCategoryId() + "라는 카테고리 ID는 없습니다.")
                ));

                categoryNameList.add(category.get().getName());
            }
            String categoryNames = categoryNameList.stream().collect(Collectors.joining(", "));

            storeResponseDtoList.add(StoreResponseDto.from(store, categoryNames));
        }

        return storeResponseDtoList;
    }

    public StoreResponseDto getStore(UUID storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new NullPointerException("해당 점포를 찾을 수 없습니다.")
        );

        List<StoreCategory> storeCategoryList = storeCategoryRepository.findAllByStoreStoreId(store.getStoreId());
        List<String> categoryNameList = new ArrayList<>();
        for (StoreCategory storeCategory : storeCategoryList){
            Optional<Category> category = Optional.ofNullable(categoryRepository.findById(storeCategory.getCategory().getCategoryId()).orElseThrow(
                    () -> new NullPointerException(storeCategory.getCategory().getCategoryId() + "라는 카테고리 ID는 없습니다.")
            ));

            categoryNameList.add(category.get().getName());
        }
        String categoryNames = categoryNameList.stream().collect(Collectors.joining(", "));

        return StoreResponseDto.from(store, categoryNames);
    }

    @Transactional
    public void deleteStore(UUID storeId, UserDetailsImpl userDetails) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new NullPointerException("존재하지않는 점포입니다.")
        );

        // OWNER의 가게인지 확인 -> 본인 가게만 수정, MANAGER와 MASTER는 검증하지 않음
        if(userDetails.getUser().getRole().equals(UserRoleEnum.OWNER)) {
            checkStoreOfOwner(storeId, userDetails);
        }

        store.setDeletedDate(LocalDateTime.now());
        store.setDeletedBy(userDetails.getUsername());
    }

    public List<StoreResponseDto> getStoreOfOwner(User user) {
        List<Store> storeList = storeRepository.findAllByUser(user);
        List<StoreResponseDto> responseDtoList = new ArrayList<>();

        for (Store store : storeList) {
            List<StoreCategory> storeCategoryList = storeCategoryRepository.findAllByStoreStoreId(store.getStoreId());
            List<String> categoryNameList = new ArrayList<>();
            for (StoreCategory storeCategory : storeCategoryList){
                Optional<Category> category = Optional.ofNullable(categoryRepository.findById(storeCategory.getCategory().getCategoryId()).orElseThrow(
                        () -> new NullPointerException(storeCategory.getCategory().getCategoryId() + "라는 카테고리 ID는 없습니다.")
                ));

                categoryNameList.add(category.get().getName());
            }
            String categoryNames = categoryNameList.stream().collect(Collectors.joining(", "));

            responseDtoList.add(StoreResponseDto.from(store, categoryNames));
        }

        return responseDtoList;
    }

    public String getNameOfOwner(UUID storeId) {
        User user = storeRepository.findById(storeId).map(Store::getUser).orElseThrow(
                () -> new IllegalArgumentException(storeId + "라는 점포가 없습니다.")
        );

        return user.getUsername();
    }

    private void checkStoreOfOwner(UUID storeId, UserDetailsImpl userDetails) {
        String ownerNameOfStore = getNameOfOwner(storeId);
        if(ownerNameOfStore.matches(userDetails.getUser().getUsername())) {
            throw new IllegalArgumentException("본인 점포만 수정이 가능합니다.");
        }
    }

    private void checkStoreNameAndPhoneNumber(String storeName, String phoneNumber) {
        Optional<Store> checkStoreName = storeRepository.findByStoreNameAndPhoneNumber(storeName, phoneNumber);
        if (checkStoreName.isPresent()) {
            throw new IllegalArgumentException("가게명 또는 전화번호가 중복되었습니다.");
        }
    }
}
