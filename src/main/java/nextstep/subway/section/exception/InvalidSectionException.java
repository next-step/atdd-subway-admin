package nextstep.subway.section.exception;

public class InvalidSectionException extends RuntimeException {
	public InvalidSectionException() {
		super("요청하신 구간을 추가할 수 없습니다.");
	}
}
