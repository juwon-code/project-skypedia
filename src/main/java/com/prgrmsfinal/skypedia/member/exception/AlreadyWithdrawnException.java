package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class AlreadyWithdrawnException extends AbstractBaseException {
    public AlreadyWithdrawnException() {
        super("이미 탈퇴한 회원입니다.", HttpStatus.BAD_REQUEST);
    }
}
