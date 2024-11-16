package com.sparta.blackwhitedeliverydriver.repository;

import com.sparta.blackwhitedeliverydriver.entity.Order;
import com.sparta.blackwhitedeliverydriver.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByUser(User user);

    Optional<Order> findByTid(String tid);
}
