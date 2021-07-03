package nextstep.subway.exception;

public class InvalidSectionException extends RuntimeException {
	public InvalidSectionException() {
	}

	public InvalidSectionException(String message) {
		super(message);
	}
}
