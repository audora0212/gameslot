// repository/TimetableEntryRepository.java
package com.example.scheduler.repository;

import com.example.scheduler.domain.TimetableEntry;
import com.example.scheduler.domain.Server;
import com.example.scheduler.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.*;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {
    List<TimetableEntry> findByServerOrderBySlot(Server server);
    List<TimetableEntry> findByServerAndGameOrderBySlot(Server server, String game);
    List<TimetableEntry> findByServerAndSlot(Server server, LocalDateTime slot);
    // 새로 추가
    Optional<TimetableEntry> findByServerAndUser(Server server, User user);
}