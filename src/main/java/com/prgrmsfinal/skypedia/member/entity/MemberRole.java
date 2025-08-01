package com.prgrmsfinal.skypedia.member.entity;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberRole {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_role_id_seq")
    @SequenceGenerator(name = "member_role_id_seq", sequenceName = "member_role_id_seq")
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private RoleType roleType;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public MemberRole(Member member, RoleType roleType) {
        this.member = member;
        this.roleType = roleType;
    }
}
