package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public enum ApiExceptionMessge {
	NOT_FOUND_LINE(HttpStatus.NOT_FOUND, "찾는 지하철 노선 정보가 없습니다."),
	NOT_FOUND_STATION(HttpStatus.NOT_FOUND, "찾는 지하철 역 정보가 없습니다."),
	OVER_DISTANCE(HttpStatus.BAD_REQUEST, "기존 거리보다 큰 거리값입니다."),
	DISTANCE_NOT_UNDER_ZERO(HttpStatus.BAD_REQUEST, "잘못된 거리값입니다."),
	EXISTS_ALL_STATIONS(HttpStatus.BAD_REQUEST, "모든 지하철 역이 등록 되어있습니다."),
	NOT_EXISTS_STATIONS(HttpStatus.BAD_REQUEST, "모든 지하철 역이 등록 되어 있지 않습니다."),
	;

	private HttpStatus status;
	private String message;

	ApiExceptionMessge(final HttpStatus status, final String message) {
		this.status = status;
		this.message = message;
	}

	public HttpStatus status() {
		return this.status;
	}

	public String message() {
		return this.message;
	}
}
