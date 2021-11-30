package nextstep.subway.exception;

import nextstep.subway.exception.dto.SubwayErrorResponse;

public class SubwayException extends RuntimeException {
	private SubwayError error;

	public SubwayException(SubwayError error) {
		this.error = error;
	}

	public SubwayException(String message, SubwayError error) {
		super(message);
		this.error = error;
	}

	public SubwayException(String message, Throwable cause, SubwayError error) {
		super(message, cause);
		this.error = error;
	}

	public SubwayException(Throwable cause, SubwayError error) {
		super(cause);
		this.error = error;
	}

	public SubwayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
		SubwayError error) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.error = error;
	}

	public SubwayErrorResponse getErrorResponse() {
		return error.toErrorResponse();
	}

	public int getStatusCode() {
		return error.getStatusCode();
	}
}
