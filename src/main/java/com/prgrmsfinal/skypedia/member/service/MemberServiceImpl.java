package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.global.constant.SearchOption;
import com.prgrmsfinal.skypedia.global.constant.SortType;
import com.prgrmsfinal.skypedia.global.dto.SearchRequestDto;
import com.prgrmsfinal.skypedia.global.dto.SearchResponseDto;
import com.prgrmsfinal.skypedia.global.exception.SearchNotFoundException;
import com.prgrmsfinal.skypedia.member.entity.MemberDocument;
import com.prgrmsfinal.skypedia.member.dto.MemberInternalDto;
import com.prgrmsfinal.skypedia.member.dto.MemberRequestDto;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDto;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.entity.MemberRole;
import com.prgrmsfinal.skypedia.member.exception.InvalidCommandException;
import com.prgrmsfinal.skypedia.member.exception.MemberException;
import com.prgrmsfinal.skypedia.member.exception.NicknameConflictException;
import com.prgrmsfinal.skypedia.member.repository.MemberDocumentRepository;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.member.util.SecurityUtil;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDto;
import com.prgrmsfinal.skypedia.photo.service.PhotoProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final MemberDocumentRepository memberDocumentRepository;
    private final MemberRoleService memberRoleService;
    private final PhotoProfileService photoProfileService;
    private final SecurityUtil securityUtil;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository
            , MemberDocumentRepository memberDocumentRepository
            , MemberRoleService memberRoleService
            , PhotoProfileService photoProfileService
            , SecurityUtil securityUtil
            , RefreshTokenService refreshTokenService
    ) {
        this.memberRepository = memberRepository;
        this.memberDocumentRepository = memberDocumentRepository;
        this.memberRoleService = memberRoleService;
        this.photoProfileService = photoProfileService;
        this.securityUtil = securityUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    @Override
    public Member create(MemberInternalDto.Create dto) {
        Member savedMember = memberRepository.save(
                Member.builder()
                .oauthId(dto.oauthId())
                .name(dto.name())
                .nickname(dto.nickname())
                .email(dto.email())
                .build()
        );

        MemberRole savedRole = memberRoleService.save(savedMember, RoleType.USER);

        memberDocumentRepository.save(MemberDocument.of(savedMember, List.of(RoleType.USER)));

        return savedMember;
    }

    @Transactional(readOnly = true)
    private Member getMemberById(Long memberId) {
        return memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new MemberException("해당 ID의 회원은 존재하지 않습니다.", HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    private Member getMemberByIdAndStatus(Long memberId, boolean removed) {
        return memberRepository.findMemberByIdAndStatus(memberId, removed)
                .orElseThrow(() -> new MemberException("해당 ID의 회원은 존재하지 않습니다.", HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    private Member getMemberProfileByIdAndStatus(Long memberId, boolean removed) {
        return memberRepository.findMemberWithRolesAndPhotoByIdAndStatus(memberId, removed)
                .orElseThrow(() -> new MemberException("해당 ID의 회원의 프로필이 존재하지 않습니다.", HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public MemberInternalDto.Profile getProfileByOAuthId(String oauthId) {
        Member member = memberRepository.findMemberWithRolesAndPhotoByOauthId(oauthId)
                .orElseThrow(NoSuchElementException::new);

        boolean isAdmin = memberRoleService.getRolesByMemberId(securityUtil.getCurrentMemberId()).contains(RoleType.ADMIN);

        if (!isAdmin && member.isRemoved()) {
            throw new MemberException("해당 회원은 탈퇴 유예기간에 있는 회원입니다.", HttpStatus.BAD_REQUEST);
        }

        List<RoleType> roleTypes = member.getMemberRoles().stream()
                .map(MemberRole::getRoleType).toList();

        String profilePhotoUrl = photoProfileService.getReadUrl(member.getPhotoProfile());

        return MemberInternalDto.Profile.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profilePhotoUrl(profilePhotoUrl)
                .roleTypes(roleTypes)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public SearchResponseDto.Pagination<MemberDocument> search(SearchRequestDto.Member requestDto) {
        String keyword = requestDto.keyword();
        SearchOption option = requestDto.searchOption();
        SortType sortType = requestDto.sortType();
        Pageable pageable = PageRequest.of(requestDto.page(), 20);

        SearchHits<MemberDocument> searchHits = memberDocumentRepository.search(keyword, option, sortType, pageable);

        if (!searchHits.hasSearchHits()) {
            throw new SearchNotFoundException();
        }

        return SearchResponseDto.Pagination.<MemberDocument>builder()
                .page(pageable.getPageNumber())
                .totalPages((int) Math.ceil((double) searchHits.getTotalHits() / pageable.getPageSize()))
                .totalCount(searchHits.getTotalHits())
                .isFirst(pageable.getPageNumber() == 0)
                .isLast(pageable.getOffset() + searchHits.getSearchHits().size() >= searchHits.getTotalHits())
                .data(searchHits.get().map(SearchHit::getContent).toList())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public MemberResponseDto.ReadProfile getMyProfile() {
        Long memberId = securityUtil.getCurrentMemberId();
        Member member = getMemberProfileByIdAndStatus(memberId, false);

        String profilePhotoUrl = photoProfileService.getReadUrl(member.getPhotoProfile());

        return MemberResponseDto.ReadProfile.builder()
                .nickname(member.getNickname())
                .email(member.getEmail())
                .profilePhotoUrl(profilePhotoUrl)
                .socialType(member.getSocialType())
                .build();
    }

    @Override
    public MemberResponseDto.ChangeProfile changeMyProfile(MemberRequestDto.ChangeProfile changeDto) {
        return changeProfile(securityUtil.getCurrentMemberId(), changeDto);
    }

    @Transactional
    @Override
    public MemberResponseDto.ChangeProfile changeProfile(Long memberId, MemberRequestDto.ChangeProfile changeDto) {
        Member member = getMemberByIdAndStatus(memberId, false);
        String nickname = changeDto.nickname();

        if (nickname != null) {
            changeNickname(member, nickname);

            updateDocument(memberId, document -> document.setNickname(nickname));
        }

        PhotoResponseDto.ProfileUpload uploadResponseDto = null;
        if (changeDto.profilePhotoData() != null) {
            uploadResponseDto = photoProfileService.upload(member, changeDto.profilePhotoData());
            if (uploadResponseDto.photoProfile() != null) {
                member.changePhotoProfile(uploadResponseDto.photoProfile());
            }
        }

        return MemberResponseDto.ChangeProfile.builder()
                .nickname(nickname)
                .uuid(uploadResponseDto != null ? uploadResponseDto.uuid() : null)
                .uploadUrl(uploadResponseDto != null ? uploadResponseDto.uploadUrl() : null)
                .build();
    }

    @Transactional
    @Override
    public MemberResponseDto.Withdraw removeMe() {
        Long memberId = securityUtil.getCurrentMemberId();

        LocalDateTime removedAt = remove(memberId);

        SecurityContextHolder.clearContext();

        updateDocument(memberId, document -> document.setRemovedAt(removedAt));

        return MemberResponseDto.Withdraw.builder()
                .removedAt(removedAt)
                .willDeletedAt(removedAt.plusDays(30))
                .build();
    }

    @Transactional
    @Override
    public LocalDateTime remove(Long memberId) {
        Member member = getMemberByIdAndStatus(memberId, false);

        member.remove();

        refreshTokenService.delete(member.getId());

        return member.getRemovedAt();
    }

    @Transactional
    @Override
    public void reset(Long memberId) {
        Member member = getMemberById(memberId);

        String nickname = generateRandomNickname();

        member.changeNickname(nickname);

        updateDocument(memberId, document -> document.setNickname(nickname));

        photoProfileService.purge(memberId);
    }

    @Transactional
    @Override
    public void restore(Long memberId) {
        Member member = getMemberByIdAndStatus(memberId, true);

        updateDocument(memberId, document -> document.setRemovedAt(null));

        member.restore();
    }

    @Transactional
    @Override
    public void purgeRemovedMembers() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30L);

        List<Long> memberIds = memberRepository.findPurgeableMemberIds(cutoff);

        photoProfileService.purgeAll(memberIds);

        Long count = memberRepository.deleteAllByIds(memberIds);

        memberDocumentRepository.deleteAll(memberIds);

        log.info("{}개의 회원 데이터가 영구적으로 삭제되었습니다.", count);
    }

    @Transactional
    @Override
    public String changeRole(Long memberId, MemberRequestDto.ChangeRole requestDto) {
        Member member = getMemberById(memberId);
        RoleType roleType = RoleType.valueOf(requestDto.roleName());
        String message = "";

        switch (requestDto.command()) {
            case "ADD" -> {
                member.addRole(roleType);
                message = "해당 회원에게 새로운 역할을 부여했습니다.";
            }
            case "REMOVE" -> {
                member.removeRole(roleType);
                message = "해당 회원에게 해당 역할을 제거했습니다.";
            }
            default -> throw new InvalidCommandException();
        }

        UpdateRolesToDocument(member);

        return message;
    }

    @Override
    public String generateRandomNickname() {
        return String.format("User%s+%s"
                , UUID.randomUUID().toString().substring(0, 10)
                , UUID.randomUUID().toString().substring(0, 5));
    }

    private void changeNickname(Member member, String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new NicknameConflictException();
        }

        member.changeNickname(nickname);

        updateDocument(member.getId(), document -> document.setNickname(nickname));
    }

    private void updateDocument(Long memberId, Consumer<MemberDocument> updater) {
        MemberDocument memberDocument = memberDocumentRepository.get(memberId);

        updater.accept(memberDocument);

        memberDocumentRepository.save(memberDocument);
    }

    private void UpdateRolesToDocument(Member member) {
        List<String> roles = member.getMemberRoles().stream()
                .map(role -> role.getRoleType().name()).toList();

        updateDocument(member.getId(), document -> document.setRoles(roles));
    }
}