package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class CannotRevokeException extends AbstractBaseException {
    public CannotRevokeException() {
        super("제거할 역할이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
    }
}
