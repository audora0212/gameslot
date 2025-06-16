package com.example.scheduler.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TimetableEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "server_id")
    private Server server;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime slot;

    @ManyToOne
    @JoinColumn(name = "default_game_id")
    private DefaultGame defaultGame;

    @ManyToOne
    @JoinColumn(name = "custom_game_id")
    private CustomGame customGame;
}
