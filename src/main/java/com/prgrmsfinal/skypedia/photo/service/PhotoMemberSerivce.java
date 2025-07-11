package com.prgrmsfinal.skypedia.photo.service;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.photo.dto.PhotoRequestDto;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDto;
import com.prgrmsfinal.skypedia.photo.entity.PhotoMember;

import java.util.List;
import java.util.Map;

public interface PhotoMemberSerivce {
    String getReadUrl(Long memberId);

    Map<Long, String> getReadUrls(List<Long> memberIds);

    PhotoResponseDto.Upload<PhotoMember> upload(Member member, PhotoRequestDto.Upload requestDto);

    void purge(Long memberId);

    void purgeAll(List<Long> memberIds);
}
