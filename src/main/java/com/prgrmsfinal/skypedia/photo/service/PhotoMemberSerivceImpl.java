package com.prgrmsfinal.skypedia.photo.service;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.photo.dto.PhotoRequestDto;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDto;
import com.prgrmsfinal.skypedia.photo.entity.PhotoMember;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PhotoMemberSerivceImpl implements PhotoMemberSerivce {
    @Override
    public String getReadUrl(Long memberId) {
        return "";
    }

    @Override
    public Map<Long, String> getReadUrls(List<Long> memberIds) {
        return new HashMap<>();
    }

    @Override
    public PhotoResponseDto.Upload<PhotoMember> upload(Member member, PhotoRequestDto.Upload requestDto) {
        return null;
    }

    @Override
    public void purge(Long memberId) {

    }

    @Override
    public void purgeAll(List<Long> memberIds) {

    }
}
