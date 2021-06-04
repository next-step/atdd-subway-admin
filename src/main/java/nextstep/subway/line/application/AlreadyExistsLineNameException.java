package nextstep.subway.line.application;

public class AlreadyExistsLineNameException extends RuntimeException {
	public AlreadyExistsLineNameException(String message) {
		super(message);
	}
}
