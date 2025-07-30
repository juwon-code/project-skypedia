package com.prgrmsfinal.skypedia.member.filter;

import com.prgrmsfinal.skypedia.member.service.JwtTokenService;
import com.prgrmsfinal.skypedia.member.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, JwtTokenService jwtTokenService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtTokenUtil.extractBearerToken(request);

        try {
            Authentication authentication = jwtTokenUtil.getAuthentication(token);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ExpiredJwtException e) {
            Claims accessClaims = e.getClaims();

            Long memberId = Long.parseLong(accessClaims.getSubject());

            List<String> roleTypes = accessClaims.get("roles", List.class);

            String refreshToken = jwtTokenService.getRefreshToken(memberId);

            if (refreshToken == null) {
                throw new ExpiredJwtException(null, null, null);
            }

            jwtTokenUtil.getClaims(refreshToken);

            String accessToken = jwtTokenUtil.createAccessToken(memberId, roleTypes);

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{ \"accessToken\": \"" + accessToken + "\" }");
        }

        filterChain.doFilter(request, response);
    }
}