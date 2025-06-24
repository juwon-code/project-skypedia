package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class NotAuthenticatedException extends AbstractBaseException {
    public NotAuthenticatedException() {
        super("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
    }
}
