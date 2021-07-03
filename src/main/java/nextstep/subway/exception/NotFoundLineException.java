package nextstep.subway.exception;

public class NotFoundLineException extends RuntimeException {
	public NotFoundLineException() {
	}

	public NotFoundLineException(String message) {
		super(message);
	}
}
