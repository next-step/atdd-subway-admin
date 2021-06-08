package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public enum ApiExceptionMessge {
	NOT_FOUND_LINE(HttpStatus.NOT_FOUND, "찾는 지하철 노선 정보가 없습니다."),
	NOT_FOUND_STATION(HttpStatus.NOT_FOUND, "찾는 지하철 역 정보가 없습니다."),
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
