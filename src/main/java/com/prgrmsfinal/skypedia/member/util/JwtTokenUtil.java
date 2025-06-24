package com.prgrmsfinal.skypedia.member.util;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.member.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

// JWT 토큰 제공자
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenUtil {
    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private SecretKey getSignedKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new InvalidTokenException("유효하지 않은 인증 토큰입니다.", HttpStatus.UNAUTHORIZED);
        }

        return bearerToken.substring(7);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        Long memberId = Long.parseLong(claims.getSubject());

        List<String> roles = claims.get("roles", List.class);

        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(memberId, null, authorities);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String createAccessToken(Long memberId, List<RoleType> roleTypes) {
        Claims claims = Jwts.claims()
                .subject(String.valueOf(memberId))
                .add("roles", roleTypes.stream()
                        .map(RoleType::toString)
                        .toList())
                .build();

        return createToken(claims, accessTokenExpiration);
    }

    public String createRefreshToken(Long memberId) {
        Claims claims = Jwts.claims()
                .subject(String.valueOf(memberId))
                .build();

        return createToken(claims, refreshTokenExpiration);
    }

    private String createToken(Claims claims, long expirationMillis) {
        return Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .claims(claims)
                .signWith(getSignedKey())
                .compact();
    }
}