package com.prgrmsfinal.skypedia.hashtag.entity;

import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Hashtag(Member member, String name) {
        this.member = member;
        this.name = name;
    }
}
