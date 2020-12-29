package nextstep.subway.line.application;

import nextstep.subway.common.NotFoundException;

public class LineNotFoundException extends NotFoundException {

	public LineNotFoundException(String message) {
		super(message);
	}

	public LineNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public LineNotFoundException(Throwable cause) {
		super(cause);
	}

	public LineNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
