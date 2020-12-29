package nextstep.subway.common.dto;

import nextstep.subway.common.exception.CommonException;

/**
 * @author : byungkyu
 * @date : 2020/12/29
 * @description :
 **/
public class ErrorResponse {
	private String errorCode;
	private String errorMessage;

	protected ErrorResponse() {
	}

	public ErrorResponse(CommonException exception) {
		this.errorCode = exception.getErrorCode();
		this.errorMessage = exception.getErrorMessage();
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
