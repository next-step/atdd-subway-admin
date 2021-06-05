package nextstep.subway.line.application.exceptions;

public class AlreadyExistsLineNameException extends RuntimeException {
	public AlreadyExistsLineNameException(String message) {
		super(message);
	}
}
