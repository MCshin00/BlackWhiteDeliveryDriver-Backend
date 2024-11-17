package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.ProductResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.Category;
import com.sparta.blackwhitedeliverydriver.entity.Product;
import com.sparta.blackwhitedeliverydriver.entity.Store;
import com.sparta.blackwhitedeliverydriver.entity.StoreCategory;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.repository.CategoryRepository;
import com.sparta.blackwhitedeliverydriver.repository.ProductRepository;
import com.sparta.blackwhitedeliverydriver.repository.StoreCategoryRepository;
import com.sparta.blackwhitedeliverydriver.repository.StoreRepository;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final ProductRepository productRepository;

    @Transactional
    public UUID createStore(@Valid StoreRequestDto requestDto, List<Category> categoryList, User user) {
        // 점포 중복확인 (이름, 전화번호)

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

        store.setDeletedDate(LocalDateTime.now());
        store.setDeletedBy(userDetails.getUsername());
    }

    public String getNameOfOwner(UUID storeId) {
        User user = storeRepository.findById(storeId).map(Store::getUser).orElseThrow(
                () -> new IllegalArgumentException(storeId + "라는 점포가 없습니다.")
        );

        return user.getUsername();
    }

    public List<StoreResponseDto> getStores(User user, int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Store> storeList = storeRepository.findAll(pageable);
        List<StoreResponseDto> storeResponseDtoList = new ArrayList<>();

        for(Store store : storeList.getContent()){
            List<StoreCategory> storeCategoryList = storeCategoryRepository.findAllByStoreStoreId(store.getStoreId());
            List<String> categoryNameList = new ArrayList<>();
            for (StoreCategory storeCategory : storeCategoryList){
                Optional<Category> category = Optional.ofNullable(categoryRepository.findById(storeCategory.getCategory().getCategoryId()).orElseThrow(
                        () -> new NullPointerException(storeCategory.getCategory().getCategoryId() + "라는 카테고리 ID는 없습니다.")
                ));

                categoryNameList.add(category.get().getName());
            }
            String categoryNames = categoryNameList.stream().collect(Collectors.joining(", "));

            StoreResponseDto storeResponseDto = StoreResponseDto.from(store, categoryNames);
            storeResponseDtoList.add(storeResponseDto);
        }

        return storeResponseDtoList;
    }

    public List<StoreResponseDto> getStoresOfOwner(User user, int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        UserRoleEnum userRoleEnum = user.getRole();

        Page<Store> storeList;
        List<StoreResponseDto> storeResponseDtoList = new ArrayList<>();
        if(userRoleEnum == UserRoleEnum.OWNER) {
            storeList = storeRepository.findAllByUser(user, pageable);

            for(Store store : storeList.getContent()){
                List<StoreCategory> storeCategoryList = storeCategoryRepository.findAllByStoreStoreId(store.getStoreId());
                List<String> categoryNameList = new ArrayList<>();
                for (StoreCategory storeCategory : storeCategoryList){
                    Optional<Category> category = Optional.ofNullable(categoryRepository.findById(storeCategory.getCategory().getCategoryId()).orElseThrow(
                            () -> new NullPointerException(storeCategory.getCategory().getCategoryId() + "라는 카테고리 ID는 없습니다.")
                    ));

                    categoryNameList.add(category.get().getName());
                }
                String categoryNames = categoryNameList.stream().collect(Collectors.joining(", "));

                StoreResponseDto storeResponseDto = StoreResponseDto.from(store, categoryNames);
                storeResponseDtoList.add(storeResponseDto);
            }
        }

        return storeResponseDtoList;
    }

    public List<StoreResponseDto> searchStores(String storeName, int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Store> storeList = storeRepository.findAllByStoreNameContaining(storeName, pageable);
        List<StoreResponseDto> storeResponseDtoList = new ArrayList<>();

        for(Store store : storeList.getContent()){
            List<StoreCategory> storeCategoryList = storeCategoryRepository.findAllByStoreStoreId(store.getStoreId());
            List<String> categoryNameList = new ArrayList<>();
            for (StoreCategory storeCategory : storeCategoryList){
                Optional<Category> category = Optional.ofNullable(categoryRepository.findById(storeCategory.getCategory().getCategoryId()).orElseThrow(
                        () -> new NullPointerException(storeCategory.getCategory().getCategoryId() + "라는 카테고리 ID는 없습니다.")
                ));

                categoryNameList.add(category.get().getName());
            }
            String categoryNames = categoryNameList.stream().collect(Collectors.joining(", "));

            StoreResponseDto storeResponseDto = StoreResponseDto.from(store, categoryNames);
            storeResponseDtoList.add(storeResponseDto);
        }

        return storeResponseDtoList;
    }
}
