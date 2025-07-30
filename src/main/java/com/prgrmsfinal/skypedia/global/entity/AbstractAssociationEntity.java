package com.prgrmsfinal.skypedia.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class AbstractAssociationEntity<ID extends Serializable, E1, E2> {
    @EmbeddedId
    protected ID id;

    @Column(insertable = false, updatable = false, nullable = false)
    protected LocalDateTime createdAt;

    protected abstract ID createId(E1 entity1, E2 entity2);

    protected void initializeId(E1 entity1, E2 entity2) {
        this.id = createId(entity1, entity2);
    }
}
