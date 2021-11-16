package nextstep.subway.line.exception;

public class IllegalSectionException extends IllegalArgumentException {

	public static final String MESSAGE = "구간은 최소 1개의 역을 가져야합니다.";

	public IllegalSectionException() {
		super(MESSAGE);
	}
}
