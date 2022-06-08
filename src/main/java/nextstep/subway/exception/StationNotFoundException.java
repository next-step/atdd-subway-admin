package nextstep.subway.exception;

public class StationNotFoundException extends RuntimeException{
	private static final long serialVersionUID = -7699758400737006501L;

	public StationNotFoundException(String msg) {
		super(msg);
	}
}
