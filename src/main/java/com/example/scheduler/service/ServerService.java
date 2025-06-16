package com.example.scheduler.service;

import com.example.scheduler.domain.Server;
import com.example.scheduler.domain.User;
import com.example.scheduler.dto.ServerDto;
import com.example.scheduler.repository.ServerRepository;
import com.example.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServerService {
    private final ServerRepository serverRepo;
    private final UserRepository userRepo;

    public ServerDto.Response create(ServerDto.CreateRequest req) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Server srv = Server.builder()
                .name(req.getName())
                .owner(owner)
                .members(Set.of(owner))
                .resetTime(req.getResetTime())
                .build();

        serverRepo.save(srv);
        return toDto(srv);
    }

    public ServerDto.Response join(Long serverId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username).orElseThrow();
        Server srv = serverRepo.findById(serverId).orElseThrow();
        if (srv.getMembers().contains(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 참가한 서버");
        }
        srv.getMembers().add(user);
        serverRepo.save(srv);
        return toDto(srv);
    }

    public ServerDto.Response updateResetTime(Long serverId, ServerDto.UpdateResetTimeRequest req) {
        Server srv = serverRepo.findById(serverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        srv.setResetTime(req.getResetTime());
        serverRepo.save(srv);
        return toDto(srv);
    }

    // 서버 목록 반환
    public List<ServerDto.Response> list() {
        return serverRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 서버 상세 정보 (멤버만)
    public ServerDto.Response getDetail(Long serverId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Server srv = serverRepo.findById(serverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!srv.getMembers().contains(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "서버 멤버가 아닙니다");
        }
        return toDto(srv);
    }

    private ServerDto.Response toDto(Server s) {
        return new ServerDto.Response(
                s.getId(),
                s.getName(),
                s.getOwner().getUsername(),
                s.getMembers().stream().map(User::getUsername).collect(Collectors.toSet()),
                s.getResetTime()
        );
    }
}