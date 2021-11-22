package nextstep.subway.line.exception;

import nextstep.subway.line.domain.Sections;

public class DeletableSectionNotFoundException extends RuntimeException {

	public static final String MESSAGE = String.format(
		"노선은 최소 %d개의 구간을 가져야 합니다.",
		Sections.SECTIONS_MIN_SIZE_INCLUSIVE
	);

	public DeletableSectionNotFoundException() {
		super(MESSAGE);
	}
}
