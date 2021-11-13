package nextstep.subway.common;

public class ErrorResponse {

	private final String message;

	private ErrorResponse(String message) {
		this.message = message;
	}

	public static ErrorResponse of(String message) {
		return new ErrorResponse(message);
	}

	public String getMessage() {
		return message;
	}
}
