package com.prgrmsfinal.skypedia.chat.entity;

import com.prgrmsfinal.skypedia.chat.entity.compositekey.ChatBlackListId;
import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatBlackList extends AbstractAssociationEntity<ChatBlackListId, Member, Member> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("targetId")
    @JoinColumn(name = "target_id", referencedColumnName = "id", nullable = false)
    private Member target;

    @Builder
    public ChatBlackList(Member member, Member target) {
        super.initializeId(member, target);
        this.member = member;
        this.target = target;
    }

    @Override
    protected ChatBlackListId createId(Member member, Member target) {
        return new ChatBlackListId(member.getId(), target.getId());
    }
}
