package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.ProductResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.Product;
import com.sparta.blackwhitedeliverydriver.entity.Store;
import com.sparta.blackwhitedeliverydriver.repository.ProductRepository;
import com.sparta.blackwhitedeliverydriver.repository.StoreRepository;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import java.util.ArrayList;
import java.util.List;
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

    public List<ProductResponseDto> getProducts(UUID storeId, UserDetailsImpl userDetails) {
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
}
