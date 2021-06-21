package nextstep.subway.line.domain;

import nextstep.subway.common.exception.ConflictException;

public final class LineNameDuplicatedException extends ConflictException {

	public LineNameDuplicatedException(String name) {
		super("Line name duplicated. " + name);
	}
}
