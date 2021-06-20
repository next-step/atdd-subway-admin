package nextstep.subway.section.exception;

import nextstep.subway.exception.BadRequestException;

public class InvalidSectionException extends BadRequestException {
	public InvalidSectionException() {
		super("요청하신 구간을 추가할 수 없습니다.");
	}
}
