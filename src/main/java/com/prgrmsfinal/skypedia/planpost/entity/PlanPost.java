package com.prgrmsfinal.skypedia.planpost.entity;

import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PlanPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "plan_post_city_id", referencedColumnName = "id", nullable = false)
    private PlanPostCity planPostCity;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(nullable = false)
    private String summary;

    @Column(nullable = false)
    private BigDecimal totalRating;

    @Column(nullable = false)
    private boolean removed;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime removedAt;

    @Builder
    public PlanPost(Member member, PlanPostCity planPostCity, String title, String summary, BigDecimal totalRating) {
        this.member = member;
        this.planPostCity = planPostCity;
        this.title = title;
        this.summary = summary;
        this.totalRating = totalRating;
        this.removed = false;
        this.removedAt = null;
    }

    public void modify(String title, String summary) {
        this.title = title;
        this.summary = summary;
    }

    public void refreshRating(BigDecimal totalRating) {
        this.totalRating = totalRating;
    }

    public void remove() {
        this.removed = true;
        this.removedAt = LocalDateTime.now();
    }
}
