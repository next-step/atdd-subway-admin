package nextstep.subway.exception;

public class NotContainSectionException extends RuntimeException {

    private static final String NOT_CONTAIN_SECTION_EXCEPTION_MESSAGE = "구간에 포함되는 역이 존재하지 않습니다.";

    public NotContainSectionException() {
        super(NOT_CONTAIN_SECTION_EXCEPTION_MESSAGE);
    }
}
