package nextstep.subway.common.exception;

public class ErrorResponse {
	private String message;

	private int code;

	public ErrorResponse() {
	}

	public ErrorResponse(ErrorCode error) {
		this.message = error.getMessage();
		this.code = error.getCode();
	}

	public String getMessage() {
		return message;
	}

	public int getCode() {
		return code;
	}
}
