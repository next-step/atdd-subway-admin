package nextstep.subway.line.domain;

public class LineNameDuplicatedException extends RuntimeException {

    public static final String MESSAGE = "이름이 동일한 노선이 존재합니다.";

    public LineNameDuplicatedException() {
        super(MESSAGE);
    }
}
