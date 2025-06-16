package com.example.scheduler.dto;

import lombok.*;
import java.time.LocalTime;
import java.util.Set;

public class ServerDto {
    @Data
    public static class CreateRequest {
        private String name;
        private LocalTime resetTime; // 생성 시 초기화 시간
    }

    @Data
    public static class UpdateResetTimeRequest {
        private LocalTime resetTime; // 변경할 초기화 시간
    }

    @Data
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private String owner;
        private Set<String> members;
        private LocalTime resetTime; // 응답에 초기화 시간 포함
    }
}