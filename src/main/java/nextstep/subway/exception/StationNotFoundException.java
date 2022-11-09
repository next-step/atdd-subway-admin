package nextstep.subway.exception;

import static java.lang.String.format;

public class StationNotFoundException extends RuntimeException {

	public StationNotFoundException(Long id) {
		super(format("지하철 역이 존재하지 않습니다. (id='%s')", id));
	}
}
