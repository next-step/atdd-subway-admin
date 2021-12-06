package nextstep.subway.exception;

public class AppException extends RuntimeException {

	private final ErrorCode errorCode;
	private final String description;

	public AppException(ErrorCode errorCode, String description) {
		super(description);
		this.errorCode = errorCode;
		this.description = description;
	}

	public ErrorCode getErrorCode() {
		return this.errorCode;
	}

	public String getDescription() {
		return this.description;
	}

}
