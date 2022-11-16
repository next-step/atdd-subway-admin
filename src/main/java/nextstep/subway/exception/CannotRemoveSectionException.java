package nextstep.subway.exception;

public class CannotRemoveSectionException extends RuntimeException {

    public static final String ONE_SECTION_REMAINS = "구간이 하나인 선에서 구간을 뺄 수 없습니다.";

    public CannotRemoveSectionException(String message) {
        super(message);
    }
}
