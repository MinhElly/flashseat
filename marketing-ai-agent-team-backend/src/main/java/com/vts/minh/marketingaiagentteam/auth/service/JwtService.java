package com.vts.minh.marketingaiagentteam.auth.service;

import com.vts.minh.marketingaiagentteam.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final Duration accessTokenTtl;

    public JwtService(JwtEncoder jwtEncoder, @Value("${spring.app.jwt.access-token-minutes:30}") long accessTokenMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.accessTokenTtl = Duration.ofMinutes(accessTokenMinutes);
    }
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("marketing-ai-agent-team")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(accessTokenTtl))
                .claim("role",user.getRole().name())
                .build();
        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();
        JwtEncoderParameters params = JwtEncoderParameters.from(header, claims);
        return jwtEncoder.encode(params).getTokenValue();
    }
    public long getAccessTokenExpiresInSeconds() {
        return accessTokenTtl.getSeconds();
    }
}
