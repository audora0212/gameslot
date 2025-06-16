// controller/ServerController.java
package com.example.scheduler.controller;

import com.example.scheduler.dto.ServerDto;
import com.example.scheduler.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}