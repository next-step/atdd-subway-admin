package nextstep.subway.line.exception;

import nextstep.subway.line.domain.Sections;

public class DeletableSectionNotFoundException extends RuntimeException {

	public static final String MESSAGE = String.format(
		"삭제할 수 있는 노선이 없습니다. 노선은 최소 %d개의 구간을 가져야 합니다.",
		Sections.SECTIONS_MIN_SIZE_INCLUSIVE
	);

	public DeletableSectionNotFoundException() {
		super(MESSAGE);
	}
}
