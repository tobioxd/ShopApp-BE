package com.shopapp.controllers;

import com.shopapp.responses.LoginResponse;
import com.shopapp.responses.RegisterResponse;
import com.shopapp.responses.UserListResponse;
import com.shopapp.services.impl.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.shopapp.dtos.*;
import com.shopapp.exceptions.DataExistAlreadyException;
import com.shopapp.exceptions.DataNotFoundException;
import com.shopapp.exceptions.ExpiredTokenException;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;
    @PostMapping("/register")
    @Operation(summary = "Register account")
    public ResponseEntity<RegisterResponse> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result) {
        RegisterResponse registerResponse = new RegisterResponse();
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDTO, result));
        } catch (DataExistAlreadyException e) {
            registerResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(registerResponse);
        } catch (Exception e) {
            registerResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registerResponse);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.loginUser(userLoginDTO));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    LoginResponse.builder().message(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    LoginResponse.builder().message(e.getMessage()).build());
        }
    }

    @PostMapping("/refreshToken")
    @Operation(summary = "Refresh Token")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        try {
            return ResponseEntity.ok(userService.refreshToken(refreshTokenDTO));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    LoginResponse.builder().message(e.getMessage()).build());
        } catch (ExpiredTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    LoginResponse.builder().message(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder().message(e.getMessage()).build());
        }
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users")
    public ResponseEntity<UserListResponse> getAllUser(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUser(keyword, page, limit));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/blockOrEnable/{userId}/{active}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Block or enable user")
    public ResponseEntity<?> blockOrEnable(
            @PathVariable Long userId,
            @PathVariable boolean active) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.blockOrEnableUser(userId, active));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
