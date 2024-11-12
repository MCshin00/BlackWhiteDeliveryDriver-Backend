package com.sparta.blackwhitedeliverydriver.repository;

import com.sparta.blackwhitedeliverydriver.entity.Address;
import com.sparta.blackwhitedeliverydriver.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findAllByUser(User user);
}
