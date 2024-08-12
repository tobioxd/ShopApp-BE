package com.shopapp.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import com.shopapp.dtos.RefreshTokenDTO;
import com.shopapp.dtos.UserDTO;
import com.shopapp.dtos.UserLoginDTO;
import com.shopapp.models.User;
import com.shopapp.responses.LoginResponse;
import com.shopapp.responses.RegisterResponse;
import com.shopapp.responses.UserListResponse;

public interface IUserService {

    RegisterResponse createUser(UserDTO userDTO, BindingResult result) throws Exception;

    LoginResponse loginUser(UserLoginDTO userLoginDTO) throws Exception;

    LoginResponse refreshToken(RefreshTokenDTO refreshTokenDTO) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User getUserDetailsFromRefreshToken(String refreshToken) throws Exception;

    Page<User> findAll(String keyword, Pageable pageable) throws Exception;

    public void blockOrEnable(Long userId, boolean active) throws Exception;

    UserListResponse getAllUser(String keyword, int page, int limit) throws Exception;

    String blockOrEnableUser(Long userId, boolean active) throws Exception;

}
