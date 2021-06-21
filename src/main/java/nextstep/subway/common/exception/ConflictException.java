package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public abstract class ConflictException extends Exception {

	public ConflictException(String message) {
		super(message);
	}
}
