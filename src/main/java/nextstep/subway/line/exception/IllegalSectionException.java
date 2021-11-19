package nextstep.subway.line.exception;

public class IllegalSectionException extends IllegalArgumentException {

	public static final String MESSAGE = "구간은 서로 다른 역으로 구성되어야 합니다.";

	public IllegalSectionException() {
		super(MESSAGE);
	}
}
