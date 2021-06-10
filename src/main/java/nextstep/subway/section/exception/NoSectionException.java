package nextstep.subway.section.exception;

public class NoSectionException extends RuntimeException {
	public NoSectionException() {
		super("섹션이 존재하지 않습니다.");
	}
}
