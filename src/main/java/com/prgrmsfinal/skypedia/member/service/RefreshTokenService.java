package com.prgrmsfinal.skypedia.member.service;

public interface RefreshTokenService {
    void save(Long memberId, String token);

    String get(Long memberId);

    void delete(Long memberId);
}
