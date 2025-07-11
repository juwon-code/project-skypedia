package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class AlreadyGrantedException extends AbstractBaseException {
    public AlreadyGrantedException() {
        super("해당 회원에게 이미 부여된 역할입니다.", HttpStatus.BAD_REQUEST);
    }
}
