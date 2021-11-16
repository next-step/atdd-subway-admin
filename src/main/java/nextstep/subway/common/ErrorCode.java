package nextstep.subway.common;

public enum ErrorCode {
	LINE_NULL_POINTER_ERROR("LINE001", "해당 노선은 존재하지 않습니다.");

	private final String errorCode;
	private final String errorMessage;

	ErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
