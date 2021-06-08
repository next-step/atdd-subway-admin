package nextstep.subway.line.exception;

public class LineNotFoundException extends RuntimeException {
	public LineNotFoundException(Long lineId) {
		super("라인이 존재하지 않습니다 lineId: " + lineId);
	}
}
