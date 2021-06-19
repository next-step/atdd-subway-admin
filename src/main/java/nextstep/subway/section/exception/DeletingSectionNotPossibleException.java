package nextstep.subway.section.exception;

import nextstep.subway.exception.BadRequestException;

public class DeletingSectionNotPossibleException extends BadRequestException {
	public DeletingSectionNotPossibleException() {
		super("구간을 삭제할 수 없습니다.");
	}
}
