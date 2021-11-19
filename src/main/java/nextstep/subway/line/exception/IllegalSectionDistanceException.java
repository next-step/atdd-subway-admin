package nextstep.subway.line.exception;

public class IllegalSectionDistanceException extends IllegalArgumentException {

	public static final String MESSAGE = "구간의 길이는 0보다 커야 합니다.";

	public IllegalSectionDistanceException() {
		super(MESSAGE);
	}
}
