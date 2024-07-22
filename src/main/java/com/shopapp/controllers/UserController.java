package com.shopapp.controllers;

import com.shopapp.models.Token;
import com.shopapp.models.User;
import com.shopapp.responses.LoginResponse;
import com.shopapp.responses.RegisterResponse;
import com.shopapp.services.TokenService;
import com.shopapp.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.shopapp.dtos.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/register")
    @Operation(summary = "Register account")
    public ResponseEntity<RegisterResponse> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result) {
        RegisterResponse registerResponse = new RegisterResponse();

        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            registerResponse.setMessage(errorMessages.toString());
            return ResponseEntity.badRequest().body(registerResponse);
        }

        if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            registerResponse.setMessage("Password do not match !");
            return ResponseEntity.badRequest().body(registerResponse);
        }

        try {
            User user = userService.createUser(userDTO);
            registerResponse.setMessage("Register successfully !");
            registerResponse.setUser(user);
            return ResponseEntity.ok(registerResponse);
        } catch (Exception e) {
            registerResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(registerResponse);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO) {
        try {
            String token = userService.loginUser(
                    userLoginDTO.getPhoneNumber(),
                    userLoginDTO.getPassword());

            User userDetail = userService.getUserDetailsFromToken(token);
            Token jwtToken = tokenService.addToken(userDetail, token);

            return ResponseEntity.ok(LoginResponse.builder()
                    .message("Login successfully !")
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(userDetail.getUsername())
                    .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                    .id(userDetail.getId())
                    .build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/refreshToken")
    @Operation(summary = "Refresh Token")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        try {
            User userDetail = userService.getUserDetailsFromRefreshToken(refreshTokenDTO.getRefreshToken());
            Token jwtToken = tokenService.refreshToken(refreshTokenDTO.getRefreshToken(), userDetail);
            return ResponseEntity.ok(LoginResponse.builder()
                    .message("Refresh token successfully")
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(userDetail.getUsername())
                    .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                    .id(userDetail.getId())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(e.getMessage())
                            .build());
        }
    }
}
