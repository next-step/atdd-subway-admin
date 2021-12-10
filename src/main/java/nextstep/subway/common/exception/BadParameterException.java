package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;

public class BadParameterException extends BaseException {
	public BadParameterException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}
}
