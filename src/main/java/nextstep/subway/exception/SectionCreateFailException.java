package nextstep.subway.exception;

public class SectionCreateFailException extends RuntimeException{

    private static final String SECTION_CREATE_FAIL_EXCEPTION_MESSAGE = "추가 할 수 없는 구간입니다.";

    public SectionCreateFailException() {
        super(SECTION_CREATE_FAIL_EXCEPTION_MESSAGE);
    }

    public SectionCreateFailException(String message) {
        super(message);
    }
}
