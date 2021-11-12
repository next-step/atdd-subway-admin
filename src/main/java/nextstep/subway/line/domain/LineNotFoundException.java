package nextstep.subway.line.domain;

public class LineNotFoundException extends RuntimeException {

    public static final String MESSAGE = "노선이 존재하지 않습니다.";

    public LineNotFoundException() {
        super(MESSAGE);
    }
}
