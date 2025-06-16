package com.example.scheduler.service;

import com.example.scheduler.domain.CustomGame;
import com.example.scheduler.domain.DefaultGame;
import com.example.scheduler.domain.Server;
import com.example.scheduler.dto.GameDto;
import com.example.scheduler.repository.CustomGameRepository;
import com.example.scheduler.repository.DefaultGameRepository;
import com.example.scheduler.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final DefaultGameRepository defaultGameRepo;
    private final CustomGameRepository customGameRepo;
    private final ServerRepository serverRepo;

    // 기본 게임 전체 조회
    public List<GameDto.DefaultGameResponse> listAllDefault() {
        return defaultGameRepo.findAll().stream()
                .map(dg -> new GameDto.DefaultGameResponse(dg.getId(), dg.getName()))
                .collect(Collectors.toList());
    }

    // 서버별 커스텀 게임 조회
    public List<GameDto.CustomGameResponse> listCustomByServer(Long serverId) {
        Server srv = serverRepo.findById(serverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "서버를 찾을 수 없습니다."));
        return customGameRepo.findByServer(srv).stream()
                .map(cg -> new GameDto.CustomGameResponse(cg.getId(), cg.getName()))
                .collect(Collectors.toList());
    }

    // 서버별 커스텀 게임 추가
    public GameDto.CustomGameResponse addCustomGame(Long serverId, GameDto.CustomGameRequest req) {
        Server srv = serverRepo.findById(serverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "서버를 찾을 수 없습니다."));
        CustomGame cg = CustomGame.builder()
                .name(req.getName())
                .server(srv)
                .build();
        cg = customGameRepo.save(cg);
        return new GameDto.CustomGameResponse(cg.getId(), cg.getName());
    }
}
