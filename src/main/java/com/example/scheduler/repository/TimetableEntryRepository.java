package com.example.scheduler.repository;

import com.example.scheduler.domain.Server;
import com.example.scheduler.domain.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {
    List<TimetableEntry> findByServerOrderBySlot(Server server);
    List<TimetableEntry> findByServerAndSlot(Server server, LocalDateTime slot);
    void deleteAllByServer(Server server); // 삭제 메서드 추가
}