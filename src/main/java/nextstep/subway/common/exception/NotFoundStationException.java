package nextstep.subway.common.exception;

public class NotFoundStationException extends RuntimeException {
	public NotFoundStationException(Long id) {
		super("해당 역이 없습니다 id=" + id);
	}
}
