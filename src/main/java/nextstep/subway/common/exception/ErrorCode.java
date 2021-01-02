package nextstep.subway.common.exception;

public enum ErrorCode {
	DISTANCE_RANGE_ERROR(700, "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음"),
	ALREADY_EXISTS_STATION_ERROR(701,  "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음"),
	NOT_INCLUDE_LINE_BOTH_STATION_ERROR(702, "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");

	private final int code;
	private final String message;

	ErrorCode(final int code, final String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
