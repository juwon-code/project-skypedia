package com.prgrmsfinal.skypedia.member.handler;

import com.prgrmsfinal.skypedia.member.exception.AuthenticationFailureException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        throw new AuthenticationFailureException("인증되지 않은 요청입니다. 로그인 후 다시 시도하세요.");
    }
}
