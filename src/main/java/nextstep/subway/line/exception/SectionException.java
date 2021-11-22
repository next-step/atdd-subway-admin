package nextstep.subway.line.exception;

import nextstep.subway.common.ErrorCode;

public class SectionException extends RuntimeException {
	public SectionException(ErrorCode errorCode) {
		super(errorCode.getErrorMessage());
	}
}
