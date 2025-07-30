package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.dto.SearchRequestDto;
import com.prgrmsfinal.skypedia.global.dto.SearchResponseDto;
import com.prgrmsfinal.skypedia.member.dto.MemberRequestDto;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDto;

public interface MemberService {
	MemberResponseDto.Info create(MemberRequestDto.SocialInfo socialInfo);

	MemberResponseDto.Info getInfo(String oauthId);

	MemberResponseDto.Profile getMyProfile();

	MemberResponseDto.Profile getProfile(Long memberId);

	SearchResponseDto.Pagination<MemberResponseDto.SearchProfile> search(SearchRequestDto.Member requestDto);

	MemberResponseDto.Modify modifyMe(MemberRequestDto.Modify requestDto);

	MemberResponseDto.Modify modify(Long memberId, MemberRequestDto.Modify requestDto);

	MemberResponseDto.Remove removeMe();

	MemberResponseDto.Remove remove(Long memberId);

	void restore(Long memberId);

	void deleteRemovedMembers();

	void grantAdmin(Long memberId);

	void revokeAdmin(Long memberId);
}
