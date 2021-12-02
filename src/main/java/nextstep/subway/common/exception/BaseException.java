package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {
	protected final HttpStatus status;

	public BaseException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
