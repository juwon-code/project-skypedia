package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class CannotRestoreMemberException extends AbstractBaseException {
    public CannotRestoreMemberException() {
        super("탈퇴하지 않은 회원은 복구할 수 없습니다.", HttpStatus.BAD_REQUEST);
    }
}
