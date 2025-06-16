// service/TimetableService.java
package com.example.scheduler.service;

import com.example.scheduler.domain.Server;
import com.example.scheduler.domain.TimetableEntry;
import com.example.scheduler.domain.User;
import com.example.scheduler.dto.TimetableDto;
import com.example.scheduler.repository.ServerRepository;
import com.example.scheduler.repository.TimetableEntryRepository;
import com.example.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimetableService {
    private final TimetableEntryRepository entryRepo;
    private final ServerRepository serverRepo;
    private final UserRepository userRepo;

    public TimetableDto.EntryResponse add(TimetableDto.EntryRequest req) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username).orElseThrow();
        Server srv = serverRepo.findById(req.getServerId()).orElseThrow();
        TimetableEntry e = TimetableEntry.builder()
                .server(srv).user(user)
                .slot(req.getSlot().truncatedTo(ChronoUnit.MINUTES))
                .game(req.getGame()).build();
        entryRepo.save(e);
        return toResp(e);
    }

    public List<TimetableDto.EntryResponse> list(Long serverId, String game, boolean sortByGame) {
        Server srv = serverRepo.findById(serverId).orElseThrow();
        List<TimetableEntry> list = (game != null)
                ? entryRepo.findByServerAndGameOrderBySlot(srv, game)
                : entryRepo.findByServerOrderBySlot(srv);
        if (sortByGame) list.sort((a,b)->a.getGame().compareTo(b.getGame()));
        return list.stream().map(this::toResp).collect(Collectors.toList());
    }

    public TimetableDto.StatsResponse stats(Long serverId) {
        Server srv = serverRepo.findById(serverId).orElseThrow();
        List<TimetableEntry> list = entryRepo.findByServerOrderBySlot(srv);
        String topGame = list.stream().collect(Collectors.groupingBy(TimetableEntry::getGame, Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
        Map<LocalDateTime, Long> slotCounts = list.stream().collect(Collectors.groupingBy(TimetableEntry::getSlot, Collectors.counting()));
        LocalDateTime peak = slotCounts.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
        double avgMinute = list.stream().mapToLong(e->e.getSlot().getHour()*60 + e.getSlot().getMinute()).average().orElse(0);
        LocalDateTime avgSlot = LocalDateTime.now().withHour((int)avgMinute/60).withMinute((int)avgMinute%60).truncatedTo(ChronoUnit.MINUTES);
        return new TimetableDto.StatsResponse(topGame, avgSlot, peak, slotCounts.get(peak).intValue());
    }

    private TimetableDto.EntryResponse toResp(TimetableEntry e) {
        TimetableDto.EntryResponse r = new TimetableDto.EntryResponse();
        r.setId(e.getId()); r.setUser(e.getUser().getUsername());
        r.setSlot(e.getSlot()); r.setGame(e.getGame());
        return r;
    }
}