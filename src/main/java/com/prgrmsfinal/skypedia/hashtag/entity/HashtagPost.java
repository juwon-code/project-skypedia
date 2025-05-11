package com.prgrmsfinal.skypedia.hashtag.entity;

import com.prgrmsfinal.skypedia.global.entity.AbstractAssociationEntity;
import com.prgrmsfinal.skypedia.hashtag.entity.compositekey.HashtagPostId;
import com.prgrmsfinal.skypedia.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HashtagPost extends AbstractAssociationEntity<HashtagPostId, Hashtag, Post> {
    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("hashtagId")
    @JoinColumn(name = "hashtag_id", referencedColumnName = "id", nullable = false)
    private Hashtag hashtag;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("postId")
    @JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
    private Post post;

    @Builder
    public HashtagPost(Hashtag hashtag, Post post) {
        super.initializeId(hashtag, post);
        this.hashtag = hashtag;
        this.post = post;
    }

    @Override
    protected HashtagPostId createId(Hashtag hashtag, Post post) {
        return new HashtagPostId(hashtag.getId(), post.getId());
    }
}
