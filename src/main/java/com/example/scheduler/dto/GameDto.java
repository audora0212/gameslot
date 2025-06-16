package com.example.scheduler.dto;

import lombok.*;
import java.util.List;

public class GameDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DefaultGameResponse {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomGameRequest {
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomGameResponse {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class DefaultGameListResponse {
        private List<DefaultGameResponse> defaultGames;
    }

    @Data
    @AllArgsConstructor
    public static class CustomGameListResponse {
        private List<CustomGameResponse> customGames;
    }
}
