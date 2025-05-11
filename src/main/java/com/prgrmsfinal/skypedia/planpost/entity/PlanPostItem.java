package com.prgrmsfinal.skypedia.planpost.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PlanPostItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "plan_post_id", referencedColumnName = "id", nullable = false)
    private PlanPost planPost;

    @Column(length = 100, nullable = false)
    private String placeId;

    @Column(nullable = false)
    private String placeName;

    @Column(nullable = false)
    private String description;

    @Column(columnDefinition = "POINT SRID 4326", nullable = false)
    private Point coordinates;

    @Column(nullable = false)
    private BigDecimal rating;

    @Column(nullable = false)
    private boolean removed;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(insertable = false, updatable = false, nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime removedAt;

    @Builder
    public PlanPostItem(PlanPost planPost, String placeId, String placeName, String description, Point coordinates
            , BigDecimal rating) {
        this.planPost = planPost;
        this.placeId = placeId;
        this.placeName = placeName;
        this.description = description;
        this.coordinates = coordinates;
        this.rating = rating;
        this.removed = false;
        this.removedAt = null;
    }

    public void modify(String placeId, String placeName, String description, Point coordinates, BigDecimal rating) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.description = description;
        this.coordinates = coordinates;
        this.rating = rating;
    }

    public void remove() {
        this.removed = true;
        this.removedAt = LocalDateTime.now();
    }
}
