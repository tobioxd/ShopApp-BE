package com.shopapp.services.interfaces;

import com.shopapp.models.Token;
import com.shopapp.models.User;
import org.springframework.stereotype.Service;

@Service

public interface ITokenService {

    Token addToken(User user, String token);

    Token refreshToken(String refreshToken, User user) throws Exception;
}
