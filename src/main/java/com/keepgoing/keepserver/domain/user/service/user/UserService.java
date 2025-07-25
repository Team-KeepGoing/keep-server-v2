package com.keepgoing.keepserver.domain.user.service.user;

import com.keepgoing.keepserver.domain.user.domain.entity.user.User;
import com.keepgoing.keepserver.domain.user.dto.UserDto;
import com.keepgoing.keepserver.domain.user.dto.request.SignupRequest;
import com.keepgoing.keepserver.domain.user.dto.request.UserInfoRequest;
import com.keepgoing.keepserver.domain.user.dto.response.ApiResponse;
import com.keepgoing.keepserver.domain.user.dto.response.JwtResponse;
import com.keepgoing.keepserver.global.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface UserService {
    ApiResponse<JwtResponse> registerUser(SignupRequest signupRequest) throws BusinessException;

    ResponseEntity<String> updateUserData(UserInfoRequest request, Authentication authentication);

    ResponseEntity<String> deleteUser(Long id);

    JwtResponse authenticateAndGenerateJWT(String email, String password);

    Optional<User> getUserByEmail(String email);
}
