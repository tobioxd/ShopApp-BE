package com.shopapp.services.interfaces;

import com.shopapp.dtos.UserDTO;
import com.shopapp.models.User;

public interface IUserService {

    User createUser(UserDTO userDTO) throws Exception;

    String loginUser(String phoneNumber, String password) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User getUserDetailsFromRefreshToken(String refreshToken) throws Exception;

}
