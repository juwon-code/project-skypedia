package com.prgrmsfinal.skypedia.votepost.entity;

import com.prgrmsfinal.skypedia.votepost.constant.VotePosition;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VotePostItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "vote_post_id", referencedColumnName = "id", nullable = false)
    private VotePost votePost;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private VotePosition position;

    @Column(nullable = false)
    private boolean won;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public VotePostItem(VotePost votePost, int positionValue) {
        if (positionValue < 1 || positionValue > 2) {
            throw new IllegalArgumentException("사진은 1번 혹은 2번에만 배치할 수 있습니다.");
        }

        this.votePost = votePost;
        this.position = VotePosition.getInstance(positionValue);
        this.won = false;
    }

    public void toggleWon() {
        this.won = !this.won;
    }
}
