package com.prgrmsfinal.skypedia.hashtag.entity;

import com.prgrmsfinal.skypedia.hashtag.entity.key.HashtagPostId;
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
public class HashtagPost {
    @EmbeddedId
    private HashtagPostId id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("hashtagId")
    @JoinColumn(name = "hashtag_id", referencedColumnName = "id", nullable = false)
    private Hashtag hashtag;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("postId")
    @JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
    private Post post;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public HashtagPost(Hashtag hashtag, Post post) {
        this.id = new HashtagPostId(hashtag.getId(), post.getId());
        this.hashtag = hashtag;
        this.post = post;
    }
}
