package nextstep.subway.exception;

public class CannotRemoveSingleSectionException extends RuntimeException {

    private static final String CANNOT_REMOVE_SINGLE_SECTION_MESSAGE = "단일 구간인 경우 삭제할 수 없습니다.";

    public CannotRemoveSingleSectionException() {
        super(CANNOT_REMOVE_SINGLE_SECTION_MESSAGE);
    }
}
