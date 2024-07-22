package com.shopapp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopapp.models.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{

    boolean existsByPhoneNumber(String username);

    Optional<User> findByPhoneNumber(String username);

}