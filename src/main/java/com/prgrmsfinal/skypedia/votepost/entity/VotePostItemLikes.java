package com.prgrmsfinal.skypedia.votepost.entity;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.votepost.entity.compositekey.VotePostItemLikesId;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VotePostItemLikes {
    @EmbeddedId
    private VotePostItemLikesId id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("votePostItemId")
    @JoinColumn(name = "vote_post_item_id", referencedColumnName = "id", nullable = false)
    private VotePostItem votePostItem;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public VotePostItemLikes(VotePostItem votePostItem, Member member) {
        this.id = new VotePostItemLikesId(votePostItem.getId(), member.getId());
        this.votePostItem = votePostItem;
        this.member = member;
    }
}
