package nextstep.subway.line.application;

import nextstep.subway.common.ValidationException;

public class SectionValidationException extends ValidationException {

	public SectionValidationException() {
	}

	public SectionValidationException(String message) {
		super(message);
	}

	public SectionValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public SectionValidationException(Throwable cause) {
		super(cause);
	}

	public SectionValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
