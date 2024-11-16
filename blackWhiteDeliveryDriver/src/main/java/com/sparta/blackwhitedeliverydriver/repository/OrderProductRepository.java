package com.sparta.blackwhitedeliverydriver.repository;

import com.sparta.blackwhitedeliverydriver.entity.Order;
import com.sparta.blackwhitedeliverydriver.entity.OrderProduct;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, UUID> {
    List<OrderProduct> findAllByOrder(Order order);
}
