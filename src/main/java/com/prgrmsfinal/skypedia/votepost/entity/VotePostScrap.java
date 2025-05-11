package com.prgrmsfinal.skypedia.votepost.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.votepost.entity.compositekey.VotePostScrapId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VotePostScrap extends AbstractAssociationEntity<VotePostScrapId, VotePost, Member> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("votePostId")
    @JoinColumn(name = "vote_post_id", referencedColumnName = "id", nullable = false)
    private VotePost votePost;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Builder
    public VotePostScrap(VotePost votePost, Member member) {
        super.initializeId(votePost, member);
        this.votePost = votePost;
        this.member = member;
    }

    @Override
    protected VotePostScrapId createId(VotePost votePost, Member member) {
        return new VotePostScrapId(votePost.getId(), member.getId());
    }
}
