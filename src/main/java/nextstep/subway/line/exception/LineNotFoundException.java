package nextstep.subway.line.exception;

public class LineNotFoundException extends RuntimeException {

	public LineNotFoundException() {
		super();
	}

	public LineNotFoundException(final String message) {
		super(message);
	}
}
