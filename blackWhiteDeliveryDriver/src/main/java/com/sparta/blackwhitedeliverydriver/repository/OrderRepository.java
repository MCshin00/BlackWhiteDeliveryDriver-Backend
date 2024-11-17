package com.sparta.blackwhitedeliverydriver.repository;

import com.sparta.blackwhitedeliverydriver.entity.Order;
import com.sparta.blackwhitedeliverydriver.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByUser(User user);
    Optional<Order> findByTid(String tid);
    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.deletedDate IS NULL")
    Page<Order> findAllByUserAndNotDeleted(User user, Pageable pageable);
}
