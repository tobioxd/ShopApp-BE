package com.shopapp.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopapp.models.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{

    boolean existsByPhoneNumber(String username);

    Optional<User> findByPhoneNumber(String username);

    @Query("SELECT o FROM User o WHERE (:keyword IS NULL OR :keyword = '' OR "+
            "LOWER(o.fullName) LIKE LOWER(CONCAT('%',:keyword,'%')) OR "+
            "LOWER(o.phoneNumber) LIKE LOWER(CONCAT('%',:keyword,'%')) OR "+
            "LOWER(o.address) LIKE LOWER(CONCAT('%',:keyword,'%'))) AND LOWER(o.role.name) = 'user'")
    Page<User> findAll(String keyword, Pageable pageable);

}