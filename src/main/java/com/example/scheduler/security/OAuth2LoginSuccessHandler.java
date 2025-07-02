package com.example.scheduler.security;

import com.example.scheduler.domain.User;
import com.example.scheduler.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtProvider;
    private final UserRepository    userRepo;

    public OAuth2LoginSuccessHandler(JwtTokenProvider jwtProvider,
                                     UserRepository userRepo) {
        this.jwtProvider = jwtProvider;
        this.userRepo    = userRepo;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest  req,
            HttpServletResponse res,
            Authentication      auth) throws IOException {

        OAuth2User oauthUser = (OAuth2User) auth.getPrincipal();
        String discordId     = oauthUser.getAttribute("id");

        // DB에서 User 조회
        User user = userRepo.findByDiscordId(discordId)
                .orElseThrow();

        // JWT 발급 (subject = username)
        String token    = jwtProvider.createToken(user.getUsername());
        String username = user.getUsername();

        // 프론트로 리다이렉트: 토큰과 사용자명 같이 넘김
        String redirectUrl = String.format(
                "http://localhost:3000/auth/discord/callback?token=%s&user=%s",
                token, URLEncoder.encode(username, StandardCharsets.UTF_8)
        );
        res.sendRedirect(redirectUrl);
    }
}

