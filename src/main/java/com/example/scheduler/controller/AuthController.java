package com.example.scheduler.controller;

import com.example.scheduler.dto.AuthDto;
import com.example.scheduler.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /* ---------- 회원가입 ---------- */
    @PostMapping("/signup")
    public ResponseEntity<AuthDto.SignupResponse> signup(@RequestBody AuthDto.SignupRequest req) {
        authService.signup(req);
        return ResponseEntity.ok(new AuthDto.SignupResponse("회원가입이 완료되었습니다"));
    }

    /* ---------- 로그인 ---------- */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDto.LoginRequest req) {
        try {
            String token = authService.login(req);
            return ResponseEntity.ok(new AuthDto.LoginResponse(token, "로그인 성공"));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "아이디 또는 비밀번호가 올바르지 않습니다"));
        }
    }

    /* ---------- 로그아웃 ---------- */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.ok(Map.of("message", "로그아웃되었습니다"));
    }
}
