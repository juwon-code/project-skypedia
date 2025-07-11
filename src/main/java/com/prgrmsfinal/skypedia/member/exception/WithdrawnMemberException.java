package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class WithdrawnMemberException extends AbstractBaseException {
    public WithdrawnMemberException() {
        super("탈퇴한 회원은 이용할 수 없는 서비스입니다.", HttpStatus.BAD_REQUEST);
    }
}
