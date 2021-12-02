package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistException extends BaseException {
	public ResourceAlreadyExistException(String message) {
		super(message, HttpStatus.CONFLICT);
	}
}
