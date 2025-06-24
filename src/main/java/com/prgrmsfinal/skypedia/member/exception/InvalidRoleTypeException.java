package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class InvalidRoleTypeException extends AbstractBaseException {
    public InvalidRoleTypeException() {
        super("유효하지 않은 회원 역할입니다.", HttpStatus.BAD_REQUEST);
    }
}
