package com.example.scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class AuthDto {
    @Data public static class SignupRequest { private String username; private String password; }
    @Data @AllArgsConstructor public static class SignupResponse { private String message; }

    @Data public static class LoginRequest  { private String username; private String password; }
    @Data @AllArgsConstructor public static class LoginResponse  {
        private String token;
        private String message;
    }
}
