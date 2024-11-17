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
    public ProductIdResponseDto createProductByOwner(CreateProductRequestDto requestDto, UserDetails userDetails) {
        Optional<User> curUser = userRepository.findById(userDetails.getUsername());

        if(curUser.get().getRole().equals(UserRoleEnum.OWNER)){
            // 가게 주인이 자신의 가게에 등록
            String nameOfStoreOwner = getNameOfOwner(requestDto.getStoreId());
            if(!nameOfStoreOwner.equals(curUser.get().getUsername())) {
                throw new IllegalArgumentException(StoreExceptionMessage.FORBIDDEN_ACCESS.getMessage());
            }
        }
        else if(curUser.get().getRole().equals(UserRoleEnum.MANAGER) || curUser.get().getRole().equals(UserRoleEnum.MASTER)){
            // 관리자가 점주의 가게 등록
            throw new IllegalArgumentException(StoreExceptionMessage.FORBIDDEN_ACCESS.getMessage());
        }

        // 음식 등록
        Store store = storeRepository.findById(requestDto.getStoreId()).orElseThrow(
                () -> new NullPointerException("해당 점포는 존재하지 않습니다.")
        );
        Product product = Product.from(requestDto, store);
        productRepository.save(product);

        ProductIdResponseDto productIdResponseDto = new ProductIdResponseDto(product.getProductId());
        return productIdResponseDto;
    }

    @Transactional
    public ProductIdResponseDto updateProduct(UUID productId, ProductRequestDto requestDto, UserDetailsImpl userDetails) {
        // OWNER의 가게인지 확인 -> 본인 가게만 수정
        UUID storeId = getStoreIdByProductId(productId);
        if(!isStoreOfOwner(storeId, userDetails)){
            throw new IllegalArgumentException(StoreExceptionMessage.FORBIDDEN_ACCESS.getMessage());
        }

        // 음식 조회
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 음식 정보입니다.")
        );

        product.update(requestDto, userDetails);
        ProductIdResponseDto productIdResponseDto = new ProductIdResponseDto(product.getProductId());

        return productIdResponseDto;
    }

    private boolean isStoreOfOwner(UUID storeId, UserDetailsImpl userDetails) {
        String ownerNameOfStore = getNameOfOwner(storeId);
        Optional<User> curUser = userRepository.findById(userDetails.getUsername());

        if(ownerNameOfStore.matches(curUser.get().getUsername())){ return true; }
        return false;
    }

    public String getNameOfOwner(UUID storeId) {
        User user = storeRepository.findById(storeId).map(Store::getUser).orElseThrow(
                () -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage())
        );

        return user.getUsername();
    }

    public UUID getStoreIdByProductId(UUID productId) {
        Store store = productRepository.findById(productId).orElseThrow(
                () -> new NullPointerException(ProductExceptionMessage.PRODUCT_NOT_FOUND.getMessage())
        ).getStore();
        return store.getStoreId();
    }

    @Transactional
    public ProductIdResponseDto deleteProduct(UUID productId, UserDetailsImpl userDetails) {
        Optional<User> curUser = userRepository.findById(userDetails.getUsername());
        Optional<Product> curProduct = productRepository.findById(productId);

        if(curUser.get().getRole().equals(UserRoleEnum.OWNER)){
            // 가게 주인이 자신의 가게에 등록
            String nameOfStoreOwner = getNameOfOwner(curProduct.get().getStore().getStoreId());
            if(!nameOfStoreOwner.equals(curUser.get().getUsername())) {
                throw new IllegalArgumentException(StoreExceptionMessage.FORBIDDEN_ACCESS.getMessage());
            }
        }
        else if(curUser.get().getRole().equals(UserRoleEnum.MANAGER) || curUser.get().getRole().equals(UserRoleEnum.MASTER)){
            // 관리자가 점주의 가게 등록
            throw new IllegalArgumentException(StoreExceptionMessage.FORBIDDEN_ACCESS.getMessage());
        }

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 음식입니다.")
        );

        product.setDeletedDate(LocalDateTime.now());
        product.setDeletedBy(userDetails.getUsername());

        ProductIdResponseDto productIdResponseDto = new ProductIdResponseDto(productId);

        return productIdResponseDto;
    }
}
