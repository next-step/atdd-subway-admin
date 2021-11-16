package nextstep.subway.line.exception;

public class DuplicatedTerminalSectionException extends IllegalArgumentException {

	public static final String MESSAGE = "종착역 구간이 이미 존재합니다.";

	public DuplicatedTerminalSectionException() {
		super(MESSAGE);
	}
}
