package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

	WRONG_INPUT("입력값을 확인해주세요", HttpStatus.BAD_REQUEST),
	DUPLICATE_INPUT("입력값이 중복입니다", HttpStatus.BAD_REQUEST),
	NOT_FOUND("해당 데이터를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
	INTERNAL_SERVER_ERROR("서버 내부 에러", HttpStatus.INTERNAL_SERVER_ERROR);

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
