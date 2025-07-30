package com.prgrmsfinal.skypedia.member.handler;

import com.prgrmsfinal.skypedia.global.constant.SocialType;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDto;
import com.prgrmsfinal.skypedia.member.service.SocialLoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;

@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    private final SocialLoginService socialLoginService;

    @Autowired
    public AuthenticationSuccessHandlerImpl(SocialLoginService socialLoginService) {
        this.socialLoginService = socialLoginService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
            , Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            try {
                String registrationId = oauth2Token.getAuthorizedClientRegistrationId();

                Map<String, Object> attributes = oauth2Token.getPrincipal().getAttributes();

                SocialType socialType = SocialType.fromString(registrationId);

                MemberResponseDto.SignIn dto = socialLoginService.authenticate(attributes, socialType);

                response.setStatus(HttpStatus.OK.value());
                response.setCharacterEncoding("UTF-8");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(String.format(
                        "{ \"nickname\": \"%s\", \"profilePhotoUrl\": \"%s\", \"accessToken\": \"%s\" }",
                        dto.nickname(), dto.photoUrl(), dto.accessToken()
                ));
            } catch (Exception e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setCharacterEncoding("UTF-8");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{ \"error\": \"로그인 중 오류가 발생했습니다. 다시 시도해주세요.\" }");
            }
        }
    }
}
