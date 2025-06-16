// repository/ServerRepository.java
package com.example.scheduler.repository;

import com.example.scheduler.domain.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server, Long> {}
