package nextstep.subway.common.exception;

public class DuplicateSectionException extends IllegalArgumentException {

	public static final String DUPLICATION_SECTION_MESSAGE = "이미 등록되어 있습니다.";

	public DuplicateSectionException() {
		super(DUPLICATION_SECTION_MESSAGE);
	}
}
