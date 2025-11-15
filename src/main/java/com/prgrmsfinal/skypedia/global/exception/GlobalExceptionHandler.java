package com.prgrmsfinal.skypedia.global.exception;

import java.util.HashMap;
import java.util.Map;

import com.prgrmsfinal.skypedia.global.dto.CommonResponseDto;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	/**
	 * 애플리케이션에서 정의한 커스텀 예외 {@link AbstractBaseException}을 처리합니다.
	 *
	 * @param e {@link AbstractBaseException} 처리 대상 예외 객체
	 * @return 예외 메시지를 포함하는 객체
	 */
	@ExceptionHandler(AbstractBaseException.class)
	public ResponseEntity<Map<String, String>> handleBaseException(AbstractBaseException e) {
		return ResponseEntity.status(e.getHttpStatus()).body(Map.of("message", e.getMessage()));
	}

	/**
	 * 컨트롤러 메소드에 {@link org.springframework.web.bind.annotation.RequestParam @RequestParam},
	 * {@link org.springframework.web.bind.annotation.PathVariable @PathVariable}등의 값 검증 실패할 경우
	 * {@link ConstraintViolationException}이 발생합니다.
	 *
	 * 이 메소드는 해당 예외를 처리하여 적절한 에러 메시지와 필드별 상세한 설명을 포함하는
	 * 응답 바디를 생성해 반환합니다.
	 *
	 * @param e {@link ConstraintViolationException} 처리 대상 예외 객체
	 * @return 응답 메시지와 유효하지 않은 필드명 및 설명을 담아 반환하는 객체
	 */
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
                        .message("요청 경로 또는 파라미터 값이 유효하지 않습니다.")
                        .details(details)
                        .build()
                );
	}

	/**
	 * 컨트롤러 메소드에 {@link org.springframework.web.bind.annotation.RequestBody @RequestBody} 파라미터로 전달된 DTO 필드
	 * 검증에 실패하면 {@link MethodArgumentNotValidException}이 발생합니다.
	 *
	 * 이 메소드는 해당 예외를 처리하여 적절한 에러 메시지와 필드별 설명을 포함하는
	 * 응답 바디를 생성해 반환합니다.
	 *
	 * @param e {@link MethodArgumentNotValidException} 처리 대상 예외 객체
	 * @return 응답 메시지와 유효하지 않은 필드명 및 설명을 담아 반환하는 객체
	 */
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
                        .message("요청 바디에 잘못된 값이 감지되었습니다.")
                        .details(details)
                        .build()
                );
	}

	/**
	 * 컨트롤러 메소드에 {@link org.springframework.web.bind.annotation.RequestParam @RequestParam},
	 * {@link org.springframework.web.bind.annotation.PathVariable @PathVariable}등의 값이
	 * 지정된 타입(Integer, Long, Enum 등)으로 변환될 수 없을 때 {@link TypeMismatchException}이 발생합니다.
	 *
	 * 이 메소드는 해당 예외를 처리하여 적절한 에러 메시지와 필드별 설명을 포함하는
	 * 응답 바디를 생성해 반환합니다.
	 *
	 * @param e {@link TypeMismatchException} 처리 대상 예외 객체
	 * @return 응답 메시지와 유효하지 않은 필드명 및 설명을 담아 반환하는 객체
	 */
	@ExceptionHandler(TypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<CommonResponseDto.ApiError> handleTypeMismatchException(TypeMismatchException e) {
		Map<String, String> details = new HashMap<>();

        String fieldName = e.getPropertyName() != null ? e.getPropertyName() : "unknown";
        String invalidValue = e.getValue() != null ? String.valueOf(e.getValue()) : "null";
        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "?";

        details.put(fieldName, String.format("'%s'는 %s 타입으로 변환할 수 없습니다.", invalidValue, requiredType));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponseDto.ApiError.builder()
                        .message("요청 파라미터의 타입이 올바르지 않습니다.")
                        .details(details)
                        .build()
                );
	}

	/**
	 * JSON 파싱 실패, DTO 필드 타입 불일치 등에서 @{link {@link HttpMessageNotReadableException}이 발생합니다.
	 *
	 * 이 메소드는 해당 예외를 처리하여 적절한 에러 메시지와 필드별 설명을 포함하는
	 * 응답 바디를 생성해 반환합니다.
	 *
	 * @param e {@link HttpMessageNotReadableException} 처리 대상 예외 객체
	 * @return 응답 메시지와 유효하지 않은 필드명 및 설명을 담아 반환하는 객체
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<CommonResponseDto.ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		Map<String, String> details = new HashMap<>();

		putUserFriendlyDetails(details, e.getMostSpecificCause().getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(CommonResponseDto.ApiError.builder()
						.message("요청 본문을 읽을 수 없습니다. JSON 형식 또는 필드 타입을 확인해주세요.")
						.details(details)
						.build()
				);
	}

	/**
	 * {@link HttpMessageNotReadableException} 객체의 원시적인 에러 메시지를 분석하고, 사용자 친화적 메시지로 변환하고,
	 * {@code details} Map에 담습니다.
	 *
	 * @param details 응답에 포함될 상세 정보를 담을 Map
	 * @param rawMessage 원시적인 예외 메시지
	 */
	private void putUserFriendlyDetails(Map<String, String> details, String rawMessage) {
		if (rawMessage == null) {
			details.put("none", "요청 본문을 처리할 수 없습니다.");
			return;
		}

		if (rawMessage.contains("Unexpected character") || rawMessage.contains("Unexpected end-of-input")) {
			details.put("none", "JSON 형식이 올바르지 않습니다.");
			return;
		}

		if (rawMessage.contains("Unrecognized field")) {
			String field = extractFieldName(rawMessage);
			details.put(field, String.format("'%s'는 허용되지 않은 필드입니다.", field));
			return;
		}

		if (rawMessage.contains("Cannot deserialize value of type") && rawMessage.contains("Integer")) {
			String field = extractFieldName(rawMessage);
			details.put(field, String.format("'%s'는 숫자여야 합니다.", field));
			return;
		}

		if (rawMessage.contains("Cannot deserialize value of type") && rawMessage.contains("LocalDate")) {
			String field = extractFieldName(rawMessage);
			details.put(field, String.format("'%s'는 올바른 날짜 형식(yyyy-MM-dd)이 아닙니다.", field));
			return;
		}

		if (rawMessage.contains("Cannot deserialize value of type") && rawMessage.contains("Enum")) {
			String field = extractFieldName(rawMessage);
			details.put(field, String.format("'%s'는 허용된 값이 아닙니다.", field));
			return;
		}

		if (rawMessage.contains("Cannot deserialize value of type") && rawMessage.contains("Boolean")) {
			String field = extractFieldName(rawMessage);
			details.put(field, String.format("'%s'는 true 또는 false여야 합니다.", field));
			return;
		}

		if (rawMessage.contains("Cannot deserialize value of type") && (rawMessage.contains("List") || rawMessage.contains("Array"))
		) {
			String field = extractFieldName(rawMessage);
			details.put(field, String.format("'%s'는 올바른 배열/리스트 형식이어야 합니다.", field));
			return;
		}

		details.put("none", "요청 본문에 올바르지 않은 값이 포함되어 있습니다.");
	}

	/**
	 * {@link HttpMessageNotReadableException} 객체의 원시적인 에러 메시지에서 필드명을 추출합니다.
	 *
	 * @param rawMessage 원시적인 예외 메시지
	 * @return 추출된 필드명 또는 "?"
	 */
	private String extractFieldName(String rawMessage) {
		if (rawMessage == null) {
			return "?";
		}

		int start = rawMessage.indexOf("\"");
		int end = rawMessage.indexOf("\"", start + 1);

		if (start == -1 || end == -1) {
			return rawMessage.substring(start + 1, end);
		}

		return "?";
	}

	/**
	 * 지원되지 않는 HTTP 메소드로 요청할 때 {@link HttpRequestMethodNotSupportedException}이 발생합니다.
	 *
	 * 이 메소드는 해당 예외를 처리하여 적절한 에러 메시지와 요청한 HTTP 메소드, 지원하는 HTTP 메소드를 포함하는
	 * 응답 바디를 생성해 반환합니다.
	 *
	 * @param e {@link HttpRequestMethodNotSupportedException} 처리 대상 예외 객체
	 * @return 응답 메시지와 요청 및 지원 HTTP 메소드를 담아 반환하는 객체
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ResponseEntity<CommonResponseDto.ApiError> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		Map<String, String> details = new HashMap<>(){{
			put("method", e.getMethod());
			put("supported", String.join(", ", e.getSupportedMethods()));
		}};

		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
				.body(CommonResponseDto.ApiError.builder()
						.message("지원되지 않는 HTTP 메소드입니다.")
						.details(details)
						.build()
				);
	}

	/**
	 * 요청 Accept 헤더와 응답 미디어 타입이 일치하지 않을 때 {@link HttpMediaTypeNotAcceptableException}이 발생합니다.
	 *
	 * 이 메소드는 해당 예외를 처리하여 적절한 에러 메시지와 지원하는 미디어 타입을 포함하는
	 * 응답 바디를 생성해 반환합니다.
	 *
	 * @param e {@link HttpMediaTypeNotAcceptableException} 처리 대상 예외 객체
	 * @return 응답 메시지와 지원하는 미디어 타입을 담아 반환하는 객체
	 */
	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	public ResponseEntity<CommonResponseDto.ApiError> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
				.body(CommonResponseDto.ApiError.builder()
						.message("요청한 미디어 타입으로 응답할 수 없습니다.")
						.details(Map.of("supported", "application/json"))
						.build()
				);
	}
}
