package com.example.scheduler.controller;

import com.example.scheduler.dto.GameDto;
import com.example.scheduler.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    // 모든 기본 게임 조회
    @GetMapping("/games/default")
    public ResponseEntity<GameDto.DefaultGameListResponse> getDefaultGames() {
        List<GameDto.DefaultGameResponse> list = gameService.listAllDefault();
        return ResponseEntity.ok(new GameDto.DefaultGameListResponse(list));
    }

    // 서버별 커스텀 게임 조회
    @GetMapping("/servers/{serverId}/custom-games")
    public ResponseEntity<GameDto.CustomGameListResponse> getCustomGames(
            @PathVariable Long serverId
    ) {
        List<GameDto.CustomGameResponse> list = gameService.listCustomByServer(serverId);
        return ResponseEntity.ok(new GameDto.CustomGameListResponse(list));
    }

    // 서버별 커스텀 게임 추가
    @PostMapping("/servers/{serverId}/custom-games")
    public ResponseEntity<GameDto.CustomGameResponse> addCustomGame(
            @PathVariable Long serverId,
            @RequestBody GameDto.CustomGameRequest req
    ) {
        GameDto.CustomGameResponse created = gameService.addCustomGame(serverId, req);
        return ResponseEntity.ok(created);
    }
}
