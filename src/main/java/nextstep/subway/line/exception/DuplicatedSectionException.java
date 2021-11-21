package nextstep.subway.line.exception;

public class DuplicatedSectionException extends IllegalArgumentException {

	public static final String MESSAGE = "이미 존재하는 구간 입니다.(중복불가)";

	public DuplicatedSectionException() {
		super(MESSAGE);
	}
}
