package com.prgrmsfinal.skypedia.member.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.global.constant.SocialType;
import com.prgrmsfinal.skypedia.photo.entity.PhotoProfile;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_id_seq")
	@SequenceGenerator(name = "member_id_seq", sequenceName = "member_id_seq")
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

	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
	private List<MemberRole> memberRoles;

	@OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
	private PhotoProfile photoProfile;

	@Builder
	public Member(String oauthId, String name, String nickname, String email) {
		this.oauthId = oauthId;
		this.name = name;
		this.nickname = nickname;
		this.email = email;
		this.removed = false;
		this.removedAt = null;
	}

	public void remove() {
		this.removed = true;
		this.removedAt = LocalDateTime.now();
	}

	public void restore() {
		this.removed = false;
		this.removedAt = null;
	}

	public void changeNickname(String nickname) {
		this.nickname = nickname;
	}

	public void changePhotoProfile(PhotoProfile photoProfile) {
		this.photoProfile = photoProfile;
	}

	public boolean addRole(RoleType roleType) {
		boolean isExists = memberRoles.stream()
				.anyMatch(memberRole -> memberRole.getRoleType().equals(roleType));

		if (isExists) {
			return false;
		}

		MemberRole memberRole = MemberRole.builder()
				.member(this)
				.roleType(roleType)
				.build();

		this.memberRoles.add(memberRole);
		return true;
	}

	public boolean removeRole(RoleType roleType) {
		boolean isSucceed = this.memberRoles.removeIf(memberRole -> memberRole.getRoleType().equals(roleType));

		return isSucceed;
	}
}
