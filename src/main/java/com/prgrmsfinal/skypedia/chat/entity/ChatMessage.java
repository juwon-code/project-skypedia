package com.prgrmsfinal.skypedia.chat.entity;

import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "chat_room_id", referencedColumnName = "id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean imaged;

    @Column(nullable = false)
    private boolean removed;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime removedAt;

    @Builder
    public ChatMessage(ChatRoom chatRoom, Member member, String content, boolean imaged) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.content = content;
        this.imaged = imaged;
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