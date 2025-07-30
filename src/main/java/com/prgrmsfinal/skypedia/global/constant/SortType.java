package com.prgrmsfinal.skypedia.global.constant;

public enum SortType {
    RELEVANCE, OLDEST, NEWEST;

    @Override
    public String toString() {
        return this.name();
    }
}
