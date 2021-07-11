package nextstep.subway.common.exception;

public class NotMatchStationException extends IllegalArgumentException {

	public static final String NOT_MATCH_STATION_MESSAGE = "구간 등록이 불가능합니다.";

	public NotMatchStationException() {
		super(NOT_MATCH_STATION_MESSAGE);
	}
}
