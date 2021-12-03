package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
	public ResourceNotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND);
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, HttpStatus.NOT_FOUND, cause);
	}
}
