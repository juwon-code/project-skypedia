package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.constant.RoleType;

import java.util.List;

public interface JwtTokenService {
    String issueAccessToken(Long memberId, List<RoleType> roleTypes);

    void issueRefreshToken(Long memberId);

    String getRefreshToken(Long memberId);

    void deleteRefreshToken(Long memberId);
}
