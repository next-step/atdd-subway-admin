package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

	WRONG_INPUT("입력값을 확인해주세요", HttpStatus.BAD_REQUEST),
	DUPLICATE_INPUT("입력값이 중복입니다", HttpStatus.BAD_REQUEST);

	private String message;
	private HttpStatus httpStatus;

	ErrorCode(String message, HttpStatus httpStatus) {
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
