package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class InvalidSocialTypeException extends AbstractBaseException {
    public InvalidSocialTypeException() {
        super("지원하지 않는 소셜 계정 타입입니다.", HttpStatus.BAD_REQUEST);
    }
}
