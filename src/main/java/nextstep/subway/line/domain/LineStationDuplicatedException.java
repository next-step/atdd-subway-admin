package nextstep.subway.line.domain;

import nextstep.subway.common.exception.ConflictException;

public class LineStationDuplicatedException extends ConflictException {

	public LineStationDuplicatedException(LineStation lineStation) {
		super("Line에 동일한 Station이 존재합니다. " + lineStation);
	}
}
