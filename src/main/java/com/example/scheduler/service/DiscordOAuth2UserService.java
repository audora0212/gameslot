package com.example.scheduler.service;

import com.example.scheduler.domain.User;
import com.example.scheduler.repository.UserRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DiscordOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepo;

    public DiscordOAuth2UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oauthUser = delegate.loadUser(req);

        String discordId = oauthUser.getAttribute("id");
        String username  = oauthUser.getAttribute("username");
        String email     = oauthUser.getAttribute("email");

        System.out.println("id : "+discordId);
        System.out.println("username : "+username);
        System.out.println("email : "+email);

        // DB에 사용자 조회 또는 생성
        User user = userRepo.findByDiscordId(discordId)
                .orElseGet(() -> {
                    User u = new User();
                    u.setDiscordId(discordId);
                    u.setUsername(username);
                    // 비밀번호 없이 OAuth 전용 사용자
                    return userRepo.save(u);
                });

        // 권한 세팅
        return new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                Map.of("id", discordId, "username", username),
                "id"
        );
    }
}
