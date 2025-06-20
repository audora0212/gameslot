package com.example.scheduler.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Server {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //서버 아이디

    @Column(nullable = false)
    private String name; //서버 이름

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private User owner; //서버장

    @ManyToMany
    @JoinTable(name = "server_members",
            joinColumns = @JoinColumn(name = "server_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> members; //서버에 참여중인 유저들

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TimetableEntry> entries; //서버에 등록된 타임테이블 기록(유저들 접속시간과 게임)

    @Column(nullable = false)
    private LocalTime resetTime; // 초기화 시간
}