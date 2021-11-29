package nextstep.subway.exception;

import nextstep.subway.exception.dto.SubwayErrorResponse;

public enum SubwayError {
	UNKNOWN_ERROR(500, "Unknown error")
	, INVALID_ARGUMENT(400, "Invalid argument in the request")
	, NOT_FOUND_DATA(404, "Data not found");

	private int statusCode;
	private String defaultMessage;

	SubwayError(int statusCode, String defaultMessage) {
		this.statusCode = statusCode;
		this.defaultMessage = defaultMessage;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public SubwayErrorResponse toErrorResponse() {
		return new SubwayErrorResponse(name().toLowerCase(), statusCode, defaultMessage);
	}
}
