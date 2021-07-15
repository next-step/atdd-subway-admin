package nextstep.subway.section.domain;

import nextstep.subway.common.exception.ConflictException;

public class SectionCannotAddException extends ConflictException {

	public SectionCannotAddException(String message) {
		super(message);
	}
}
