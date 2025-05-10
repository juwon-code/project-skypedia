package com.prgrmsfinal.skypedia.votepost.entity;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.votepost.entity.compositekey.VotePostScrapId;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VotePostScrap {
    @EmbeddedId
    private VotePostScrapId id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("votePostId")
    @JoinColumn(name = "vote_post_id", referencedColumnName = "id", nullable = false)
    private VotePost votePost;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public VotePostScrap(VotePost votePost, Member member) {
        this.id = new VotePostScrapId(votePost.getId(), member.getId());
        this.votePost = votePost;
        this.member = member;
    }
}
