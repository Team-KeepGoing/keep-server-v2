package com.keepgoing.keepserver.domain.user.controller;

import com.keepgoing.keepserver.domain.user.dto.request.LoginRequest;
import com.keepgoing.keepserver.domain.user.dto.request.SignupRequest;
import com.keepgoing.keepserver.domain.user.service.user.UserService;
import com.keepgoing.keepserver.global.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저", description = "유저 관련 api 입니다.")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().body(userService.authenticateAndGenerateJWT(loginRequest.email(), loginRequest.password()));
    }

    @Operation(summary = "회원가입", description = "회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> registerAndAuthenticateUser(@RequestBody SignupRequest signupRequest) throws BusinessException {
        return ResponseEntity.ok().body(userService.registerUser(signupRequest));
    }

    @Operation(summary = "회원탈퇴", description = "회원탈퇴를 진행합니다.")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> withdrawMember(@PathVariable Long userId){
        return ResponseEntity.ok().body(userService.deleteUser(userId));
    }
}
