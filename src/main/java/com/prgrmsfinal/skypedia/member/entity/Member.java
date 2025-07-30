package com.prgrmsfinal.skypedia.member.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.prgrmsfinal.skypedia.global.constant.SocialType;
import com.prgrmsfinal.skypedia.photo.entity.PhotoMember;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_id_seq")
	@SequenceGenerator(name = "member_id_seq", sequenceName = "member_id_seq")
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(unique = true, nullable = false)
	private String oauthId;

	@Column(nullable = false)
	private String name;

	@Column(length = 20, unique = true, nullable = false)
	private String nickname;

	@Column(length = 50, unique = true, nullable = false)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(length = 6, nullable = false)
	private SocialType socialType;

	@Column(nullable = false)
	private boolean removed;

	@Column(insertable = false, updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(insertable = false, updatable = false, nullable = false)
	private LocalDateTime updatedAt;

	private LocalDateTime removedAt;

	@OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
	private PhotoMember photoMember;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MemberRole> roles;

	@Builder
	public Member(String oauthId, String name, String nickname, String email, SocialType socialType
			, PhotoMember photoMember, List<MemberRole> roles) {
		this.oauthId = oauthId;
		this.name = name;
		this.nickname = nickname;
		this.socialType = socialType;
		this.email = email;
		this.removed = false;
		this.removedAt = null;
		this.photoMember = photoMember;
		this.roles = roles != null ? roles : new ArrayList<>();
	}

	public void grantRole(MemberRole role) {
		this.roles.add(role);
	}
}
