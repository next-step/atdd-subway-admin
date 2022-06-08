package nextstep.subway.exception;

public class StationNotFoundException extends RuntimeException{
	private static final long serialVersionUID = -7699758400737006501L;

	public StationNotFoundException() {
		super("역 정보를 찾지 못했습니다.");
	}
}
