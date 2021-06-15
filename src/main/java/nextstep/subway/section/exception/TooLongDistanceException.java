package nextstep.subway.section.exception;

public class TooLongDistanceException extends RuntimeException {
	public TooLongDistanceException() {
		super("새로운 구간의 역 사이의 길이가 너무 깁니다.");
	}
}
