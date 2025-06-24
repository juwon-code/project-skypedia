package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.dto.SearchRequestDto;
import com.prgrmsfinal.skypedia.global.dto.SearchResponseDto;
import com.prgrmsfinal.skypedia.member.entity.MemberDocument;
import com.prgrmsfinal.skypedia.member.dto.MemberInternalDto;
import com.prgrmsfinal.skypedia.member.dto.MemberRequestDto;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDto;
import com.prgrmsfinal.skypedia.member.entity.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface MemberService {
	Member create(MemberInternalDto.Create member);

	MemberInternalDto.Profile getProfileByOAuthId(String oauthId);

	MemberResponseDto.ReadProfile getMyProfile();

	MemberResponseDto.ChangeProfile changeMyProfile(MemberRequestDto.ChangeProfile changeDto);

	MemberResponseDto.ChangeProfile changeProfile(Long memberId, MemberRequestDto.ChangeProfile changeDto);

	MemberResponseDto.Withdraw removeMe();

	SearchResponseDto.Pagination<MemberDocument> search(SearchRequestDto.Member requestDto);

	LocalDateTime remove(Long memberId);

	void reset(Long memberId);

	void restore(Long memberId);

	String changeRole(Long memberId, MemberRequestDto.ChangeRole requestDto);

	String generateRandomNickname();

	void purgeRemovedMembers();
}
