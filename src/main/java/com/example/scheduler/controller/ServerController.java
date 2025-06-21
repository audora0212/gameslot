// controller/ServerController.java
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

    /* ---------- 생성·참가 ---------- */

    @PostMapping
    public ResponseEntity<ServerDto.Response> create(@RequestBody ServerDto.CreateRequest req) {
        return ResponseEntity.ok(serverService.create(req));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<ServerDto.Response> join(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.join(id));
    }

    /* ---------- 일반 수정 ---------- */

    @PutMapping("/{id}/reset-time")
    public ResponseEntity<ServerDto.Response> updateResetTime(
            @PathVariable Long id,
            @RequestBody ServerDto.UpdateResetTimeRequest req) {
        return ResponseEntity.ok(serverService.updateResetTime(id, req));
    }

    @PutMapping("/{id}/name")                           // ⭐ 서버 이름 변경
    public ResponseEntity<ServerDto.Response> rename(
            @PathVariable Long id,
            @RequestBody ServerDto.UpdateNameRequest req) {
        return ResponseEntity.ok(serverService.rename(id, req));
    }

    /* ---------- 관리자 기능 ---------- */

    @PostMapping("/{id}/kick")                          // 멤버 강퇴
    public ResponseEntity<ServerDto.Response> kick(
            @PathVariable Long id,
            @RequestBody ServerDto.KickRequest req) {
        return ResponseEntity.ok(serverService.kick(id, req));
    }

    @PostMapping("/{id}/admins")                        // 관리자 임명/해제
    public ResponseEntity<ServerDto.Response> updateAdmin(
            @PathVariable Long id,
            @RequestBody ServerDto.AdminRequest req) {
        return ResponseEntity.ok(serverService.updateAdmin(id, req));
    }

    @DeleteMapping("/{id}")                             // 서버 삭제
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serverService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ---------- 조회 ---------- */

    @GetMapping
    public ResponseEntity<List<ServerDto.Response>> list() {
        return ResponseEntity.ok(serverService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServerDto.Response> detail(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.getDetail(id));
    }
}
