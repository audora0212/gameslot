// service/GameService.java
package com.example.scheduler.service;

import com.example.scheduler.domain.CustomGame;
import com.example.scheduler.domain.DefaultGame;
import com.example.scheduler.domain.Server;
import com.example.scheduler.domain.User;
import com.example.scheduler.dto.GameDto;
import com.example.scheduler.repository.CustomGameRepository;
import com.example.scheduler.repository.DefaultGameRepository;
import com.example.scheduler.repository.ServerRepository;
import com.example.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepo;

    /* ---------- 기본 / 커스텀 게임 조회 ---------- */

    public List<GameDto.DefaultGameResponse> listAllDefault() {
        return defaultGameRepo.findAll().stream()
                .map(dg -> new GameDto.DefaultGameResponse(dg.getId(), dg.getName()))
                .collect(Collectors.toList());
    }

    public List<GameDto.CustomGameResponse> listCustomByServer(Long serverId) {
        Server srv = serverRepo.findById(serverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "서버를 찾을 수 없습니다"));
        return customGameRepo.findByServer(srv).stream()
                .map(cg -> new GameDto.CustomGameResponse(cg.getId(), cg.getName()))
                .collect(Collectors.toList());
    }

    /* ---------- 커스텀 게임 추가 / 삭제 ---------- */

    public GameDto.CustomGameResponse addCustomGame(Long serverId, GameDto.CustomGameRequest req) {
        Server srv = serverRepo.findById(serverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "서버를 찾을 수 없습니다"));

        CustomGame cg = CustomGame.builder()
                .name(req.getName())
                .server(srv)
                .build();

        cg = customGameRepo.save(cg);
        return new GameDto.CustomGameResponse(cg.getId(), cg.getName());
    }

    public void deleteCustomGame(Long serverId, Long gameId) {          // ⭐ 커스텀 게임 삭제
        Server srv = serverRepo.findById(serverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "서버를 찾을 수 없습니다"));
        User me = userRepo.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow();

        if (!(srv.getOwner().equals(me) || srv.getAdmins().contains(me))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다");
        }

        CustomGame cg = customGameRepo.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게임을 찾을 수 없습니다"));
        if (!cg.getServer().equals(srv)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 서버의 게임이 아닙니다");
        }
        customGameRepo.delete(cg);
    }
}
