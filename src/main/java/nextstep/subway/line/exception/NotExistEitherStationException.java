package nextstep.subway.line.exception;

public class NotExistEitherStationException extends IllegalArgumentException {
    private static final String DEFAULT_MESSAGE = "상행역과 하행역 둘 중 하나도 포함되어 있지 않습니다.";

    public NotExistEitherStationException() {
        super(DEFAULT_MESSAGE);
    }
}
