package com.prgrmsfinal.skypedia.chat.entity;

import com.prgrmsfinal.skypedia.chat.entity.compositekey.ChatParticipantId;
import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatParticipant {
    @EmbeddedId
    private ChatParticipantId id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("chatRoomId")
    @JoinColumn(name = "chat_room_id", referencedColumnName = "id")
    private ChatRoom chatRoom;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public ChatParticipant(ChatRoom chatRoom, Member member) {
        this.id = new ChatParticipantId(chatRoom.getId(), member.getId());
        this.chatRoom = chatRoom;
        this.member = member;
    }
}
