// service/ServerService.java
package com.example.scheduler.service;

import com.example.scheduler.domain.Server;
import com.example.scheduler.domain.User;
import com.example.scheduler.dto.ServerDto;
import com.example.scheduler.repository.ServerRepository;
import com.example.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServerService {
    private final ServerRepository serverRepo;
    private final UserRepository userRepo;

    public ServerDto.Response create(ServerDto.CreateRequest req) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepo.findByUsername(username).orElseThrow();
        Server srv = Server.builder().name(req.getName()).owner(owner).members(Set.of(owner)).build();
        serverRepo.save(srv);
        return toDto(srv);
    }

    public ServerDto.Response join(Long serverId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username).orElseThrow();
        Server srv = serverRepo.findById(serverId).orElseThrow();
        srv.getMembers().add(user);
        serverRepo.save(srv);
        return toDto(srv);
    }

    private ServerDto.Response toDto(Server s) {
        ServerDto.Response res = new ServerDto.Response();
        res.setId(s.getId()); res.setName(s.getName());
        res.setOwner(s.getOwner().getUsername());
        res.setMembers(s.getMembers().stream().map(User::getUsername).collect(Collectors.toSet()));
        return res;
    }
}
