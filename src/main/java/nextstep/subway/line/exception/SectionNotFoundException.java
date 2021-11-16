package nextstep.subway.line.exception;

import nextstep.subway.common.EntityNotFoundException;

public class SectionNotFoundException extends EntityNotFoundException {

	public static final String MESSAGE = "존재하지 않는 구간입니다.";

	public SectionNotFoundException() {
		super(MESSAGE);
	}
}
