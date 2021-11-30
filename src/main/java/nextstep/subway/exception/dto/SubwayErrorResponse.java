package nextstep.subway.exception.dto;

public class SubwayErrorResponse {
	private final String errorCode;
	private final int statusCode;
	private final String message;

	public SubwayErrorResponse(String errorCode, int statusCode, String message) {
		this.errorCode = errorCode;
		this.statusCode = statusCode;
		this.message = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getMessage() {
		return message;
	}
}
