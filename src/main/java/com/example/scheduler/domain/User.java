package com.example.scheduler.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User { // Discord OAuth 추가 이후 필요한 항목들 추가
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //유저 아이디

    @Column(unique = true, nullable = false)
    private String username; //유저 이름

    @Column(nullable = true)
    private String password; //비밀번호

    @Column(unique=true)
    private String discordId;

    @ManyToMany(mappedBy = "members")
    private Set<Server> joinedServers; //참여중인 서버
}