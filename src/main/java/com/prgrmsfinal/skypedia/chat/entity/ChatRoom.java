package com.prgrmsfinal.skypedia.chat.entity;

import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private boolean removed;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime removedAt;

    @Builder
    public ChatRoom(Member member) {
        this.member = member;
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
}