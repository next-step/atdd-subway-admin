package nextstep.subway.line.application;

public class LineNotFoundException extends RuntimeException {

	public LineNotFoundException(String message) {
		super(message);
	}
}
