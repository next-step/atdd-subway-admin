package nextstep.subway.line.exception;

public class DuplicateLineNameException extends IllegalArgumentException {
    private static final String DEFAULT_MESSAGE = "노선의 이름이 중복되었습니다.";

    public DuplicateLineNameException() {
        super(DEFAULT_MESSAGE);
    }
}
