// service/ServerService.java
package com.example.scheduler.service;

import com.example.scheduler.domain.Server;
import com.example.scheduler.domain.User;
import com.example.scheduler.dto.ServerDto;
import com.example.scheduler.repository.ServerRepository;
import com.example.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServerService {

    private final ServerRepository serverRepo;
    private final UserRepository userRepo;

    /* ---------- 생성 / 참가 ---------- */
    /** 내가 참여한 서버들만 조회 */
    public List<ServerDto.Response> listMine() {
        User me = currentUser();
        return serverRepo.findByMembersContains(me).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ServerDto.Response> search(String q, int page, int size) {
        Page<Server> pg = serverRepo.findByNameContainingIgnoreCase(
                q == null ? "" : q,
                PageRequest.of(page, size)
        );
        return pg.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    public ServerDto.Response create(ServerDto.CreateRequest req) {
        User owner = currentUser();

        Server srv = Server.builder()
                .name(req.getName())
                .owner(owner)
                .members(Set.of(owner))
                .admins(Set.of(owner))          // ⭐ owner → admins
                .resetTime(req.getResetTime())
                .build();

        serverRepo.save(srv);
        return toDto(srv);
    }

    public ServerDto.Response join(Long serverId) {
        User user = currentUser();
        Server srv = fetch(serverId);

        if (srv.getMembers().contains(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 참가한 서버입니다");
        }
        srv.getMembers().add(user);
        serverRepo.save(srv);
        return toDto(srv);
    }

    /* ---------- 일반 수정 ---------- */

    public ServerDto.Response updateResetTime(Long id, ServerDto.UpdateResetTimeRequest req) {
        Server srv = fetch(id);
        assertAdmin(srv, currentUser());

        srv.setResetTime(req.getResetTime());
        serverRepo.save(srv);
        return toDto(srv);
    }

    public ServerDto.Response rename(Long id, ServerDto.UpdateNameRequest req) { // ⭐ 서버 이름 변경
        Server srv = fetch(id);
        assertAdmin(srv, currentUser());

        srv.setName(req.getName());
        serverRepo.save(srv);
        return toDto(srv);
    }

    /* ---------- 관리자 기능 ---------- */

    public ServerDto.Response kick(Long id, ServerDto.KickRequest req) {
        Server srv = fetch(id);
        User me = currentUser();
        assertAdmin(srv, me);

        User target = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (srv.getOwner().equals(target))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "서버장은 강퇴할 수 없습니다");

        srv.getMembers().remove(target);
        srv.getAdmins().remove(target);
        serverRepo.save(srv);
        return toDto(srv);
    }

    public ServerDto.Response updateAdmin(Long id, ServerDto.AdminRequest req) {
        Server srv = fetch(id);
        User me = currentUser();
        assertAdmin(srv, me);

        User target = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!srv.getMembers().contains(target))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "서버 멤버가 아닙니다");

        if (req.isGrant()) {                 // 임명
            srv.getAdmins().add(target);
        } else {                             // 해제
            if (srv.getOwner().equals(target))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "서버장은 항상 관리자입니다");
            srv.getAdmins().remove(target);
        }
        serverRepo.save(srv);
        return toDto(srv);
    }

    public void delete(Long id) {            // ⭐ 서버 삭제
        Server srv = fetch(id);
        User me = currentUser();

        if (!srv.getOwner().equals(me))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "서버장만 삭제할 수 있습니다");

        serverRepo.delete(srv);
    }

    /* ---------- 조회 ---------- */

    public List<ServerDto.Response> list() {
        return serverRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ServerDto.Response getDetail(Long id) {
        Server srv = fetch(id);
        User me = currentUser();
        if (!srv.getMembers().contains(me))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "서버 멤버가 아닙니다");

        return toDto(srv);
    }

    /* ---------- 내부 도우미 ---------- */

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username).orElseThrow();
    }

    private Server fetch(Long id) {
        return serverRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private void assertAdmin(Server srv, User user) {
        if (!(srv.getOwner().equals(user) || srv.getAdmins().contains(user))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다");
        }
    }

    private ServerDto.Response toDto(Server s) {
        List<ServerDto.MemberInfo> mems = s.getMembers().stream()
                .map(u -> new ServerDto.MemberInfo(u.getId(), u.getUsername()))
                .collect(Collectors.toList());

        List<ServerDto.MemberInfo> adms = s.getAdmins().stream()
                .map(u -> new ServerDto.MemberInfo(u.getId(), u.getUsername()))
                .collect(Collectors.toList());

        return new ServerDto.Response(
                s.getId(),
                s.getName(),
                s.getOwner().getUsername(),
                mems,
                adms,
                s.getResetTime()
        );
    }
}
