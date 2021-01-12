package nextstep.subway.common.exception;

import nextstep.subway.station.domain.Station;

public class NotFoundSectionException extends RuntimeException {
	public NotFoundSectionException(Long id) {
		super("해당 구간이 없습니다 id=" + id);
	}
}
