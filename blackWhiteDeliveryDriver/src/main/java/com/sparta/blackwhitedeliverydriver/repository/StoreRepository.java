package com.sparta.blackwhitedeliverydriver.repository;

import com.sparta.blackwhitedeliverydriver.entity.Store;
import com.sparta.blackwhitedeliverydriver.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, UUID>{

    Optional<Store> findByStoreNameAndPhoneNumber(String storeName, String phoneNumber);

    Page<Store> findAllByUser(User user, Pageable pageable);
}
