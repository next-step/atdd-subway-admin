package nextstep.subway.common.exception.dto;

public class ErrorResponse {

	private final String message;
	private String debugMessage;
	private final String path;

	public ErrorResponse(final String message, final String debugMessage, final String path) {
		this.message = message;
		this.debugMessage = debugMessage;
		this.path = path;
	}

	public String getMessage() {
		return message;
	}

	public String getDebugMessage() {
		return debugMessage;
	}

	public String getPath() {
		return path;
	}

}
