package com.prgrmsfinal.skypedia.hashtag.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.hashtag.entity.compositekey.HashtagVotePostId;
import com.prgrmsfinal.skypedia.votepost.entity.VotePost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HashtagVotePost extends AbstractAssociationEntity<HashtagVotePostId, Hashtag, VotePost> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("hashtagId")
    @JoinColumn(name = "hashtag_id", referencedColumnName = "id", nullable = false)
    private Hashtag hashtag;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("votePostId")
    @JoinColumn(name = "vote_post_id", referencedColumnName = "id", nullable = false)
    private VotePost votePost;

    @Builder
    public HashtagVotePost(Hashtag hashtag, VotePost votePost) {
        super.initializeId(hashtag, votePost);
        this.hashtag = hashtag;
        this.votePost = votePost;
    }

    @Override
    protected HashtagVotePostId createId(Hashtag hashtag, VotePost votePost) {
        return new HashtagVotePostId(hashtag.getId(), votePost.getId());
    }
}
