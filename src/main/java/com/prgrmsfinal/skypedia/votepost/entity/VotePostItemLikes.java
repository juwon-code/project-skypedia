package com.prgrmsfinal.skypedia.votepost.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.votepost.entity.compositekey.VotePostItemLikesId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VotePostItemLikes extends AbstractAssociationEntity<VotePostItemLikesId, VotePostItem, Member> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("votePostItemId")
    @JoinColumn(name = "vote_post_item_id", referencedColumnName = "id", nullable = false)
    private VotePostItem votePostItem;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Builder
    public VotePostItemLikes(VotePostItem votePostItem, Member member) {
        super.initializeId(votePostItem, member);
        this.votePostItem = votePostItem;
        this.member = member;
    }

    @Override
    protected VotePostItemLikesId createId(VotePostItem votePostItem, Member member) {
        return new VotePostItemLikesId(votePostItem.getId(), member.getId());
    }
}
