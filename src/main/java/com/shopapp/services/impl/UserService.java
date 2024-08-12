package com.shopapp.services.impl;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.shopapp.components.JwtTokenUtil;
import com.shopapp.dtos.RefreshTokenDTO;
import com.shopapp.dtos.UserDTO;
import com.shopapp.dtos.UserLoginDTO;
import com.shopapp.exceptions.DataExistAlreadyException;
import com.shopapp.exceptions.DataNotFoundException;
import com.shopapp.models.Role;
import com.shopapp.models.User;
import com.shopapp.models.Token;
import com.shopapp.repositories.RoleRepository;
import com.shopapp.repositories.TokenRepository;
import com.shopapp.repositories.UserRepository;
import com.shopapp.responses.LoginResponse;
import com.shopapp.responses.RegisterResponse;
import com.shopapp.responses.UserListResponse;
import com.shopapp.responses.UserResponse;
import com.shopapp.services.interfaces.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Override
    @Transactional
    public RegisterResponse createUser(UserDTO userDTO, BindingResult result) throws Exception {

        RegisterResponse registerResponse = new RegisterResponse();

        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            throw new Exception(errorMessages.toString());
        }

        if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            throw new Exception("Password do not match !");
        }

        String phoneNumber = userDTO.getPhoneNumber();
        // check if phone number exists
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataExistAlreadyException("Phone number exists already !");
        }

        Role role = roleRepository.findById(2L)
                .orElseThrow(() -> new DataNotFoundException("Role not found !"));

        // convert from userDTO => user
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .active(true)
                .role(role)
                .build();

        if (userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }

        userRepository.save(newUser);
        registerResponse.setMessage("Register successfully !");
        registerResponse.setUser(newUser);
        return registerResponse;
    }

    @Override
    public LoginResponse loginUser(UserLoginDTO userLoginDTO) throws Exception {
        String phoneNumber = userLoginDTO.getPhoneNumber();
        String password = userLoginDTO.getPassword();

        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if (user.isEmpty()) {
            throw new DataNotFoundException("Invalid phonenuber/password !");
        }

        User existinguser = user.get();

        if (!passwordEncoder.matches(password, existinguser.getPassword())) {
            throw new DataNotFoundException("Invalid phonenuber/password !");
        }

        User existingUser = user.orElseThrow(() -> new DataNotFoundException("Invalid phonenuber/password !"));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(phoneNumber,
                password, existingUser.getAuthorities());

        authenticationManager.authenticate(authenticationToken);
        String token = jwtTokenUtil.generateToken(existinguser);

        User userDetail = getUserDetailsFromToken(token);
        Token jwtToken = tokenService.addToken(userDetail, token);

        return LoginResponse.builder()
                .message("Login successfully !")
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                .id(userDetail.getId())
                .build();

    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }
        String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        if (user.isPresent()) {

            if (user.get().isActive() == false) {
                throw new Exception("User is blocked !");
            }

            return user.get();
        } else {
            throw new Exception("User not found");
        }
    }

    @Override
    public User getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        return getUserDetailsFromToken(existingToken.getToken());
    }

    @Override
    public Page<User> findAll(String keyword, Pageable pageable) throws Exception {
        return userRepository.findAll(keyword, pageable);
    }

    @Override
    @Transactional
    public void blockOrEnable(Long userId, boolean active) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found !"));
        existingUser.setActive(active);
        userRepository.save(existingUser);
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenDTO refreshTokenDTO) throws Exception {

        User userDetail = getUserDetailsFromRefreshToken(refreshTokenDTO.getRefreshToken());
        Token jwtToken = tokenService.refreshToken(refreshTokenDTO.getRefreshToken(), userDetail);

        return LoginResponse.builder()
                .message("Refresh token successfully")
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                .id(userDetail.getId())
                .build();

    }

    @Override
    public UserListResponse getAllUser(String keyword, int page, int limit) throws Exception {

        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").descending());

        Page<UserResponse> users = findAll(keyword, pageRequest).map(UserResponse::fromUser);

        int totalPages = users.getTotalPages();
        List<UserResponse> userResponses = users.getContent();

        return UserListResponse.builder()
                .users(userResponses)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public String blockOrEnableUser(Long userId, boolean active) throws Exception {
        blockOrEnable(userId, active);
        return active ? "User is enabled !" : "User is blocked !";
    }

}