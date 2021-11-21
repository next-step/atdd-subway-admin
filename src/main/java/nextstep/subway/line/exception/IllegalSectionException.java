package nextstep.subway.line.exception;

public class IllegalSectionException extends IllegalArgumentException {

	public static final String MESSAGE = "구간은 서로 다른 2개의 역으로 구성되어야 하고, 노선 내의 역을 포함해야 합니다.";

	public IllegalSectionException() {
		super(MESSAGE);
	}
}
