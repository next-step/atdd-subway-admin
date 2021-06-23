package nextstep.subway.exception;

public class SectionDeleteFailException extends RuntimeException{

    private static final String SECTION_DELETE_FAIL_EXCEPTION_MESSAGE = "삭제 할 수 없는 구간입니다.";

    public SectionDeleteFailException() {
        super(SECTION_DELETE_FAIL_EXCEPTION_MESSAGE);
    }

    public SectionDeleteFailException(String message) {
        super(message);
    }
}
