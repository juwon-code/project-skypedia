package com.prgrmsfinal.skypedia.global.exception;

import java.util.HashMap;
import java.util.Map;

import com.prgrmsfinal.skypedia.global.dto.CommonResponseDto;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(AbstractBaseException.class)
	public ResponseEntity<Map<String, String>> handleBaseException(AbstractBaseException e) {
		return ResponseEntity.status(e.getHttpStatus()).body(Map.of("message", e.getMessage()));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<CommonResponseDto.ApiError> handleConstraintViolationException(ConstraintViolationException e) {
		Map<String, String> details = new HashMap<>();

		e.getConstraintViolations().forEach(violation -> {
			String path = violation.getPropertyPath().toString();
			String fieldName = path.contains(".") ? path.substring(path.lastIndexOf(".") + 1) : path;
            String message = violation.getMessage();
			details.put(fieldName, message);
		});

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponseDto.ApiError.builder()
                        .message("요청 바디에 잘못된 값이 감지되었습니다.")
                        .details(details)
                        .build()
                );
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<CommonResponseDto.ApiError> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		Map<String, String> details = new HashMap<>();

		e.getBindingResult().getFieldErrors().forEach(error -> {
			details.put(error.getField(), error.getDefaultMessage());
		});

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponseDto.ApiError.builder()
                        .message("요청 쿼리에 잘못된 값이 감지되었습니다.")
                        .details(details)
                        .build()
                );
	}

	@ExceptionHandler(TypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<CommonResponseDto.ApiError> handleTypeMismatchException(TypeMismatchException e) {
		Map<String, String> details = new HashMap<>();

        String fieldName = e.getPropertyName();
        String invalidValue = String.valueOf(e.getValue());
        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "?";

        details.put(fieldName, String.format("'%s'는 %s 타입으로 변환할 수 없습니다.", invalidValue, requiredType));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponseDto.ApiError.builder()
                        .message("요청 타입이 잘못되었습니다.")
                        .details(details)
                        .build()
                );
	}
}
