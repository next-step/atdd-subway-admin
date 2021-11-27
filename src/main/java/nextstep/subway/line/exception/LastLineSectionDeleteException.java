package nextstep.subway.line.exception;

public class LastLineSectionDeleteException extends IllegalArgumentException {
    private static final String DEFAULT_MESSAGE = "마지막 구간의 역은 삭제 할 수 없습니다.";

    public LastLineSectionDeleteException() {
        super(DEFAULT_MESSAGE);
    }
}
