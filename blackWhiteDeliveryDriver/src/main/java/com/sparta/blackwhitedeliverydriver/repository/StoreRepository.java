package com.sparta.blackwhitedeliverydriver.repository;

import com.sparta.blackwhitedeliverydriver.entity.Store;
import com.sparta.blackwhitedeliverydriver.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, UUID>{

    Optional<Store> findByStoreName(@NotBlank String storeName);

    Page<Store> findAllByDeletedDateIsNullAndDeletedByIsNull(Pageable pageable);

    Optional<Store> findByIdAndDeletedDateIsNullAndDeletedByIsNull(UUID storeId);

    Page<Store> findAllByUserAndDeletedDateIsNullAndDeletedByIsNull(User user, Pageable pageable);

    Page<Store> findAllByStoreNameContainingAndDeletedDateIsNullAndDeletedByIsNull(String storeName, Pageable pageable);
}
