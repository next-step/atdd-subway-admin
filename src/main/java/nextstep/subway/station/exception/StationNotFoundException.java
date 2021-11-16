package nextstep.subway.station.exception;

import nextstep.subway.common.EntityNotFoundException;

public class StationNotFoundException extends EntityNotFoundException {

	public static final String MESSAGE = "존재하지 않는 역입니다.";

	public StationNotFoundException() {
		super(MESSAGE);
	}
}
