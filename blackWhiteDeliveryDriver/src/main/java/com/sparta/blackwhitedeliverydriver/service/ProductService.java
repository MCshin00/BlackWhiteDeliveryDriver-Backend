package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.CreateProductRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.ProductRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.ProductResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.Product;
import com.sparta.blackwhitedeliverydriver.entity.Store;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.repository.ProductRepository;
import com.sparta.blackwhitedeliverydriver.repository.StoreRepository;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import jakarta.validation.constraints.Null;
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
public class ProductService {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public List<ProductResponseDto> getProducts(UUID storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 점포입니다.")
        );
        List<Product> productList = productRepository.findAllByStore(store);
        List<ProductResponseDto> productResponseDtoList = new ArrayList<>();

        for (Product product : productList) {
            productResponseDtoList.add(ProductResponseDto.from(product));
        }

        return productResponseDtoList;
    }

    @Transactional
    public UUID createProduct(CreateProductRequestDto requestDto, User user) {
        // 음식 등록
        Store store = storeRepository.findById(requestDto.getStoreId()).orElseThrow(
                () -> new NullPointerException("해당 점포는 존재하지 않습니다.")
        );
        Product product = Product.from(requestDto, store);
        productRepository.save(product);

        return product.getProductId();
    }

    public UUID findStoreIdByProductId(UUID productId) {
        Store store = productRepository.findById(productId).orElseThrow(
                () -> new NullPointerException("음식 정보를 찾을 수 없습니다.")
        ).getStore();
        return store.getStoreId();
    }

    @Transactional
    public UUID updateProduct(UUID productId, ProductRequestDto requestDto, UserDetailsImpl userDetails) {
        // 음식 조회
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 음식 정보입니다.")
        );

        product.update(requestDto, userDetails);

        return product.getProductId();
    }
}
