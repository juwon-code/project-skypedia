package com.prgrmsfinal.skypedia.global.constant;

public enum SearchOption {
    MEMBER_ALL, MEMBER_NICKNAME, MEMBER_EMAIL;

    @Override
    public String toString() {
        return this.name();
    }
}
