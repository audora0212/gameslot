package com.example.scheduler.repository;

import com.example.scheduler.domain.TimetableEntry;
import com.example.scheduler.domain.Server;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {
    // 서버 기준으로 슬롯 오름차순 조회
    List<TimetableEntry> findByServerOrderBySlot(Server server);

    // 특정 슬롯에 있는 엔트리 조회 (필요 시 사용)
    List<TimetableEntry> findByServerAndSlot(Server server, LocalDateTime slot);
}
