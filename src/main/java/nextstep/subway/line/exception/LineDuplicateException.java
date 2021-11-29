package nextstep.subway.line.exception;

public class LineDuplicateException extends RuntimeException {
    public static final String LINE_DUPLICATE = "이미 생성된 노선입니다.";

    public LineDuplicateException() {
        super(LINE_DUPLICATE);
    }
}
