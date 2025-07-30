package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.global.constant.SearchOption;
import com.prgrmsfinal.skypedia.global.constant.SortType;
import com.prgrmsfinal.skypedia.global.dto.SearchRequestDto;
import com.prgrmsfinal.skypedia.global.dto.SearchResponseDto;
import com.prgrmsfinal.skypedia.global.exception.SearchNotFoundException;
import com.prgrmsfinal.skypedia.member.entity.MemberDocument;
import com.prgrmsfinal.skypedia.member.dto.MemberRequestDto;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDto;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.entity.MemberRole;
import com.prgrmsfinal.skypedia.member.exception.*;
import com.prgrmsfinal.skypedia.member.repository.MemberDocumentRepository;
import com.prgrmsfinal.skypedia.member.repository.MemberQueryRepository;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.member.util.SecurityUtil;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDto;
import com.prgrmsfinal.skypedia.photo.entity.PhotoMember;
import com.prgrmsfinal.skypedia.photo.service.PhotoMemberSerivce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
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
    private final MemberQueryRepository memberQueryRepository;
    private final MemberDocumentRepository memberDocumentRepository;
    private final MemberRoleService memberRoleService;
    private final PhotoMemberSerivce photoMemberSerivce;
    private final SecurityUtil securityUtil;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository
            , MemberQueryRepository memberQueryRepository
            , MemberDocumentRepository memberDocumentRepository
            , MemberRoleService memberRoleService
            , PhotoMemberSerivce photoMemberSerivce
            , SecurityUtil securityUtil
            , JwtTokenService jwtTokenService
    ) {
        this.memberRepository = memberRepository;
        this.memberQueryRepository = memberQueryRepository;
        this.memberDocumentRepository = memberDocumentRepository;
        this.memberRoleService = memberRoleService;
        this.photoMemberSerivce = photoMemberSerivce;
        this.securityUtil = securityUtil;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    @Transactional
    public MemberResponseDto.Info create(MemberRequestDto.SocialInfo socialInfo) {
        Member member = Member.builder()
                .oauthId(socialInfo.oauthId())
                .name(socialInfo.name())
                .email(socialInfo.email())
                .nickname(generateRandomNickname())
                .photoMember(null)
                .socialType(socialInfo.socialType())
                .build();

        MemberRole role = MemberRole.builder()
                .member(member)
                .roleType(RoleType.USER)
                .build();

        member.grantRole(role);

        Member savedMember = memberRepository.save(member);

        saveDocument(savedMember, List.of(role.getRoleType()));

        return MemberResponseDto.Info.builder()
                .id(savedMember.getId())
                .nickname(savedMember.getNickname())
                .roleTypes(List.of(role.getRoleType()))
                .photoUrl(null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDto.Info getInfo(String oauthId) {
        Member member = getMemberBy(
                MemberRequestDto.SearchOptions.builder()
                        .oauthId(oauthId)
                        .build()
        );

        if (member.isRemoved()) {
            throw new WithdrawnMemberException();
        }

        return MemberResponseDto.Info.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .roleTypes(member.getRoles().stream().map(MemberRole::getRoleType).toList())
                .photoUrl(photoMemberSerivce.getReadUrl(member.getId()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDto.Profile getMyProfile() {
        Long memberId = securityUtil.getCurrentMemberId();

        MemberResponseDto.Profile result = getProfile(memberId);

        if (result.removed()) {
            throw new WithdrawnMemberException();
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDto.Profile getProfile(Long memberId) {
        Member member = getMemberBy(
                MemberRequestDto.SearchOptions.builder()
                        .memberId(memberId)
                        .build()
        );

        return MemberResponseDto.Profile.builder()
                .nickname(member.getNickname())
                .email(member.getEmail())
                .socialType(member.getSocialType())
                .photoUrl(photoMemberSerivce.getReadUrl(member.getId()))
                .removed(member.isRemoved())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SearchResponseDto.Pagination<MemberResponseDto.SearchProfile> search(SearchRequestDto.Member requestDto) {
        String keyword = requestDto.keyword();
        SearchOption option = requestDto.searchOption();
        SortType sortType = requestDto.sortType();
        Pageable pageable = PageRequest.of(requestDto.page(), 20);

        SearchHits<MemberDocument> searchHits = memberDocumentRepository.findBy(keyword, option, sortType, pageable);

        if (!searchHits.hasSearchHits()) {
            throw new SearchNotFoundException();
        }

        List<MemberDocument> contents = searchHits.get().map(SearchHit::getContent).toList();

        List<Long> memberIds = contents.stream().map(MemberDocument::getId).toList();

        Map<Long, String> photoUrls = photoMemberSerivce.getReadUrls(memberIds);

        return SearchResponseDto.Pagination.<MemberResponseDto.SearchProfile>builder()
                .page(pageable.getPageNumber())
                .totalPages((int) Math.ceil((double) searchHits.getTotalHits() / pageable.getPageSize()))
                .totalCount(searchHits.getTotalHits())
                .isFirst(pageable.getPageNumber() == 0)
                .isLast(pageable.getOffset() + searchHits.getSearchHits().size() >= searchHits.getTotalHits())
                .data(contents.stream().map(content -> MemberResponseDto.SearchProfile.builder()
                        .id(content.getId())
                        .nickname(content.getNickname())
                        .email(content.getEmail())
                        .socialType(content.getSocialType())
                        .photoUrl(photoUrls.getOrDefault(content.getId(), null))
                        .roleTypes(content.getRoles())
                        .createdAt(content.getCreatedAt())
                        .removedAt(content.getRemovedAt())
                        .build()
                ).toList())
                .build();
    }

    @Override
    @Transactional
    public MemberResponseDto.Modify modifyMe(MemberRequestDto.Modify requestDto) {
        Long memberId = securityUtil.getCurrentMemberId();

        return modify(memberId, requestDto);
    }

    @Override
    @Transactional
    public MemberResponseDto.Modify modify(Long memberId, MemberRequestDto.Modify requestDto) {
        Member member = getMemberBy(
                MemberRequestDto.SearchOptions.builder()
                        .memberId(memberId)
                        .build()
        );

        if (member.isRemoved()) {
            throw new WithdrawnMemberException();
        }

        String nickname = requestDto.nickname();
        if (nickname != null) {
            if (memberQueryRepository.existsByNickname(nickname)) {
                throw new NicknameConflictException();
            }

            member.setNickname(nickname);
            updateDocument(memberId, document -> document.setNickname(nickname));
        }

        PhotoResponseDto.Upload<PhotoMember> uploadData = null;
        if (requestDto.photoData() != null) {
            uploadData = photoMemberSerivce.upload(member, requestDto.photoData());
            member.setPhotoMember(uploadData.photoMember());
        }

        return MemberResponseDto.Modify.builder()
                .nickname(nickname)
                .uuid(uploadData != null ? uploadData.uuid() : null)
                .uploadUrl(uploadData != null ? uploadData.uploadUrl() : null)
                .build();
    }

    @Override
    @Transactional
    public MemberResponseDto.Remove removeMe() {
        Long memberId = securityUtil.getCurrentMemberId();

        MemberResponseDto.Remove responseDto = remove(memberId);

        SecurityContextHolder.clearContext();

        return responseDto;
    }

    @Override
    @Transactional
    public MemberResponseDto.Remove remove(Long memberId) {
        Member member = getMemberBy(
                MemberRequestDto.SearchOptions.builder()
                        .memberId(memberId)
                        .build()
        );

        if (member.isRemoved()) {
            throw new AlreadyWithdrawnException();
        }

        LocalDateTime removedAt = LocalDateTime.now();

        member.setRemoved(true);
        member.setRemovedAt(removedAt);

        updateDocument(memberId, document -> document.setRemovedAt(removedAt));

        jwtTokenService.deleteRefreshToken(member.getId());

        return MemberResponseDto.Remove.builder()
                .removedAt(removedAt)
                .willDeletedAt(removedAt.plusDays(30))
                .build();
    }

    @Transactional
    @Override
    public void restore(Long memberId) {
        Member member = getMemberBy(
                MemberRequestDto.SearchOptions.builder()
                        .memberId(memberId)
                        .build()
        );

        if (!member.isRemoved()) {
            throw new CannotRestoreMemberException();
        }

        member.setRemoved(false);
        member.setRemovedAt(null);

        updateDocument(memberId, document -> document.setRemovedAt(null));
    }

    @Override
    @Transactional
    public void deleteRemovedMembers() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30L);

        List<Long> memberIds = memberQueryRepository.deleteAllByCutoff(cutoff);

        memberDocumentRepository.deleteAll(memberIds);
    }

    @Override
    @Transactional
    public void grantAdmin(Long memberId) {
        Member member = getMemberBy(
                MemberRequestDto.SearchOptions.builder()
                        .memberId(memberId)
                        .removed(false)
                        .build()
        );

        memberRoleService.create(member, RoleType.ADMIN);

        updateDocument(memberId, document -> {
            List<String> roles = document.getRoles();
            roles.add(RoleType.ADMIN.name());
            document.setRoles(roles);
        });
    }

    @Override
    @Transactional
    public void revokeAdmin(Long memberId) {
        Member member = getMemberBy(
                MemberRequestDto.SearchOptions.builder()
                        .memberId(memberId)
                        .removed(false)
                        .build()
        );

        memberRoleService.delete(member, RoleType.ADMIN);

        updateDocument(memberId, document -> {
            List<String> roles = document.getRoles();
            roles.remove(RoleType.ADMIN.name());
            document.setRoles(roles);
        });
    }

    private Member getMemberBy(MemberRequestDto.SearchOptions options) {
        return memberQueryRepository.findOneBy(options)
                .orElseThrow(MemberNotFoundException::new);
    }

    private String generateRandomNickname() {
        return String.format("User%s+%s"
                , UUID.randomUUID().toString().substring(0, 10)
                , UUID.randomUUID().toString().substring(0, 5));
    }

    private void saveDocument(Member member, List<RoleType> roles) {
        MemberDocument memberDocument = MemberDocument.of(member, roles);

        memberDocumentRepository.save(memberDocument);
    }

    private void updateDocument(Long memberId, Consumer<MemberDocument> updater) {
        MemberDocument memberDocument = memberDocumentRepository.get(memberId);

        updater.accept(memberDocument);

        memberDocumentRepository.save(memberDocument);
    }
}