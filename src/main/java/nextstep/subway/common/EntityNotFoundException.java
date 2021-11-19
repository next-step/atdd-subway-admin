package nextstep.subway.common;

public abstract class EntityNotFoundException extends RuntimeException {
	protected EntityNotFoundException(String message) {
		super(message);
	}
}
