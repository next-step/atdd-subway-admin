package nextstep.subway.station.application;

public class StationNotFoundException extends RuntimeException {

	public StationNotFoundException(String message) {
		super(message);
	}

	public StationNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public StationNotFoundException(Throwable cause) {
		super(cause);
	}

	public StationNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
