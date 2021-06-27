package nextstep.subway.exception;

public class DuplicateSectionException extends RuntimeException {

    private static final String DUPLICATE_SECTION_EXCEPTION_MESSAGE = "중복되는 구간은 추가할 수 없습니다.";

    public DuplicateSectionException() {
        super(DUPLICATE_SECTION_EXCEPTION_MESSAGE);
    }
}
