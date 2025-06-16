package com.example.scheduler.controller;

import com.example.scheduler.dto.ServerDto;
import com.example.scheduler.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerController {
    private final ServerService serverService;

    @PostMapping
    public ResponseEntity<ServerDto.Response> create(@RequestBody ServerDto.CreateRequest req) {
        return ResponseEntity.ok(serverService.create(req));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<ServerDto.Response> join(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.join(id));
    }

    @PutMapping("/{id}/reset-time")
    public ResponseEntity<ServerDto.Response> updateResetTime(
            @PathVariable Long id,
            @RequestBody ServerDto.UpdateResetTimeRequest req
    ) {
        return ResponseEntity.ok(serverService.updateResetTime(id, req));
    }

    // 서버 목록 조회
    @GetMapping
    public ResponseEntity<List<ServerDto.Response>> list() {
        return ResponseEntity.ok(serverService.list());
    }

    // 서버 상세 정보 조회 (멤버만 허용)
    @GetMapping("/{id}")
    public ResponseEntity<ServerDto.Response> detail(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.getDetail(id));
    }
}