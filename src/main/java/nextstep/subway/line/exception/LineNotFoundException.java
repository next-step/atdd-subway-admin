package nextstep.subway.line.exception;

import nextstep.subway.common.EntityNotFoundException;

public class LineNotFoundException extends EntityNotFoundException {

	public static final String MESSAGE = "존재하지 않는 노선입니다.";

	public LineNotFoundException() {
		super(MESSAGE);
	}
}
