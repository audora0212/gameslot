package com.example.scheduler.repository;

import com.example.scheduler.domain.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalTime;
import java.util.List;

public interface ServerRepository extends JpaRepository<Server, Long> {
    List<Server> findByResetTime(LocalTime resetTime);
}