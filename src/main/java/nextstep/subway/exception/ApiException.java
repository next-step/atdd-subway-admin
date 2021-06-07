package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

	private HttpStatus status;

	public ApiException(final ApiExceptionMessge value) {
		super(value.message());
		this.status = value.status();
	}

	public HttpStatus status() {
		return this.status;
	}
}
