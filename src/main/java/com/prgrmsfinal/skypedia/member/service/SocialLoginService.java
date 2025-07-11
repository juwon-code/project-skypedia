package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.constant.SocialType;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDto;

import java.util.Map;

public interface SocialLoginService {
    MemberResponseDto.SignIn authenticate(Map<String, Object> attributes, SocialType socialType);
}
