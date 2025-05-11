package com.prgrmsfinal.skypedia.chat.entity;

import com.prgrmsfinal.skypedia.chat.entity.compositekey.ChatParticipantId;
import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatParticipant extends AbstractAssociationEntity<ChatParticipantId, ChatRoom, Member> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("chatRoomId")
    @JoinColumn(name = "chat_room_id", referencedColumnName = "id")
    private ChatRoom chatRoom;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @Builder
    public ChatParticipant(ChatRoom chatRoom, Member member) {
        super.initializeId(chatRoom, member);
        this.chatRoom = chatRoom;
        this.member = member;
    }

    @Override
    protected ChatParticipantId createId(ChatRoom chatRoom, Member member) {
        return new ChatParticipantId(chatRoom.getId(), member.getId());
    }
}
