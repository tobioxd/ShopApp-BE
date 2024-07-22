package com.shopapp.repositories;

import com.shopapp.models.Token;
import com.shopapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findByUser(User user);

    Token findByToken(String token);
    
    Token findByRefreshToken(String token);
}
