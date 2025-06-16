package com.example.scheduler.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

public class TimetableDto {
    @Data public static class EntryRequest { private Long serverId; private LocalDateTime slot; private String game; }
    @Data public static class EntryResponse { private Long id; private String user; private LocalDateTime slot; private String game; }

    @Data
    @AllArgsConstructor
    public static class StatsResponse {
        private String topGame;
        private LocalDateTime avgSlot;
        private LocalDateTime peakSlot;
        private int peakCount;
    }
}
