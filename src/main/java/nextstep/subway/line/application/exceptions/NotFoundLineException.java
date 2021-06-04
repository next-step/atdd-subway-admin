package nextstep.subway.line.application.exceptions;

public class NotFoundLineException extends RuntimeException {
	public NotFoundLineException(String message) {
		super(message);
	}
}
