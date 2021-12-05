package nextstep.subway.common.exception;

public class EmptySectionException extends RuntimeException {
    public static final String EMPTY_SECTION_MESSAGE = "구간을 찾을 수 없습니다.";
    private static final long serialVersionUID = 8L;
    public EmptySectionException() {
        super(EMPTY_SECTION_MESSAGE);
    }
}
