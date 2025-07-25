package com.keepgoing.keepserver.domain.user.service.user;

import com.keepgoing.keepserver.domain.user.domain.entity.user.User;
import com.keepgoing.keepserver.domain.user.domain.repository.user.UserRepository;
import com.keepgoing.keepserver.domain.user.dto.request.SignupRequest;
import com.keepgoing.keepserver.domain.user.dto.request.UserInfoRequest;
import com.keepgoing.keepserver.domain.user.dto.response.ApiResponse;
import com.keepgoing.keepserver.domain.user.dto.response.JwtResponse;
import com.keepgoing.keepserver.domain.user.security.jwt.JwtUtils;
import com.keepgoing.keepserver.domain.user.security.service.UserDetailsImpl;
import com.keepgoing.keepserver.global.exception.BusinessException;
import com.keepgoing.keepserver.global.exception.error.ErrorCode;
import com.keepgoing.keepserver.global.exception.user.UserError;
import com.keepgoing.keepserver.global.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public ApiResponse<JwtResponse> registerUser(SignupRequest signupRequest) throws BusinessException {
        validateEmail(signupRequest.email());
        User user = createUser(signupRequest);
        userRepository.save(user);
        JwtResponse jwtResponse = authenticateAndGenerateJWT(signupRequest.email(), signupRequest.password());
        return ApiResponse.setApiResponse(true, "회원 가입이 완료 되었습니다!", jwtResponse);
    }

    @Override
    @Transactional
    public ResponseEntity<String> updateUserData(UserInfoRequest request, Authentication authentication) {
        String userEmail = getEmailFromAuthentication(authentication);
        User user = findUserByEmail(userEmail);
        updateUser(user, request);
        return ResponseEntity.ok().body("");
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok().body("회원탈퇴 성공");
        } catch (Exception ex) {
            throw new RuntimeException("회원탈퇴 실패", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email){
        return Optional.ofNullable(userRepository.findByEmail(email)
                                                 .orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND)));
    }

    /* 인증 및 JWT 토큰 생성 */
    @Override
    public JwtResponse authenticateAndGenerateJWT(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        checkTeacherApproval(userDetails);

        String jwt = jwtUtils.generateJwtToken(authentication);
        return JwtResponse.setJwtResponse(jwt, userDetails.getId(), userDetails.getEmail(), userDetails.getName(), userDetails.isTeacher());
    }

    private void checkTeacherApproval(UserDetailsImpl userDetails) {
        if (userDetails.isTeacher()) {
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));

            if (!user.isApproved()) {
                throw new BusinessException(UserError.TEACHER_ACCOUNT_NOT_APPROVED);
            }
        }
    }

    private String getNameByAuthentication(Authentication authentication) {
        return authentication.getName();
    }

    private void validateEmail(String email) throws BusinessException {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_BAD_REQUEST);
        }
    }

    private User createUser(SignupRequest signupRequest) {
        return User.registerUser(
                signupRequest.email(),
                encoder.encode(signupRequest.password()),
                signupRequest.name(),
                signupRequest.isTeacher(),
                signupRequest.isApproved()
        );
    }

    private String getEmailFromAuthentication(Authentication authentication) {
        return getNameByAuthentication(authentication);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmailEquals(email)
                .orElseThrow(UserException::userNotFound);
    }

    private void updateUser(User user, UserInfoRequest request) {
        user.fixUserData(request.email(), request.name());
    }

}
