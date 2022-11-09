package nextstep.subway.exception;

import static java.lang.String.format;

public class LineNotFoundException extends RuntimeException {

	public LineNotFoundException(Long id) {
		super(format("노선이 존재하지 않습니다. (id=%s)", id));
	}
}
