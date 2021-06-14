package nextstep.subway.exception;

public enum CommonExceptionMessage {
	EXISTS_ALL_STATIONS("모든 지하철 역이 등록 되어있습니다."),
	NOT_EXISTS_STATIONS("모든 지하철 역이 등록 되어 있지 않습니다."),
	OVER_DISTANCE("기존 거리보다 큰 거리값입니다."),
	DISTANCE_NOT_UNDER_ZERO("잘못된 거리값입니다."),
	CANNOT_DELETE_LAST_SECTION("하나 뿐인 구간은 삭제가 불가능합니다.");

	private String message;

	CommonExceptionMessage(final String message) {
		this.message = message;
	}

	public String message() {
		return this.message;
	}
}
