package nextstep.subway.common.exception;

public class InvalidDistanceException extends IllegalArgumentException {

	public static final String NOT_MATCH_STATION_MESSAGE = "구간 등록이 불가능합니다.";

	public InvalidDistanceException() {
		super(NOT_MATCH_STATION_MESSAGE);
	}
}
