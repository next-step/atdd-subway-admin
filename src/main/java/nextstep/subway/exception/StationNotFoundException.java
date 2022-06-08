package nextstep.subway.exception;

public class StationNotFoundException {
	public StationNotFoundException(String msg) {
		throw new RuntimeException(msg);
	}
}
