package nextstep.subway.common.exception;

public class NotFoundLineException extends RuntimeException {
	public NotFoundLineException(Long id) {
		super("해당 노선이 없습니다 id=" + id);
	}
}
