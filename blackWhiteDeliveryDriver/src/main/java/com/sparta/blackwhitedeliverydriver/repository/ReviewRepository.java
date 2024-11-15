package com.sparta.blackwhitedeliverydriver.repository;

import com.sparta.blackwhitedeliverydriver.entity.Review;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findAllByOrderStoreStoreId(UUID storeId, Pageable pageable);
    Page<Review> findAllByOrderUserUsername(String username, Pageable pageable);
}
