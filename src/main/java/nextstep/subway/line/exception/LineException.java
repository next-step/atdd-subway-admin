package nextstep.subway.line.exception;

import nextstep.subway.common.ErrorCode;

public class LineException extends RuntimeException {
	public LineException(ErrorCode errorCode) {
		super(errorCode.getErrorMessage());
	}
}