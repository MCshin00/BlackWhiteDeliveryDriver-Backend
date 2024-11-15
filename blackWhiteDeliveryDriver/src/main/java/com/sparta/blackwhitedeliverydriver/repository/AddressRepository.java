package com.sparta.blackwhitedeliverydriver.repository;

import com.sparta.blackwhitedeliverydriver.entity.Address;
import com.sparta.blackwhitedeliverydriver.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    @Query("SELECT a FROM Address a WHERE a.user = :user AND a.deletedBy IS NULL AND a.deletedDate IS NULL")
    Page<Address> findAllByUserAndNotDeleted(@Param("user") User user, Pageable pageable);
}
