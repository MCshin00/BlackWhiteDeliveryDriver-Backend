package com.sparta.blackwhitedeliverydriver.repository;

import com.sparta.blackwhitedeliverydriver.entity.Basket;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, UUID> {
}
