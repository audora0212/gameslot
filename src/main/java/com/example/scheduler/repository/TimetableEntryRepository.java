package com.example.scheduler.repository;

import com.example.scheduler.domain.Server;
import com.example.scheduler.domain.TimetableEntry;
import com.example.scheduler.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

// 추가 ↓
import java.util.Optional;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {
    List<TimetableEntry> findByServerOrderBySlot(Server server);
    List<TimetableEntry> findByServerAndSlot(Server server, LocalDateTime slot);

    // ✅ 유저 1명-당 1개 엔트리를 찾기 위한 메서드
    Optional<TimetableEntry> findByServerAndUser(Server server, User user);

    void deleteAllByServer(Server server);
}
