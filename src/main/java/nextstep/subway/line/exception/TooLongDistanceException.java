package nextstep.subway.line.exception;

public class TooLongDistanceException extends IllegalArgumentException {
    private static final String DEFAULT_MESSAGE = "기존 역 사이 길이보다 큰 거리 입니다.";

    public TooLongDistanceException() {
        super(DEFAULT_MESSAGE);
    }
}
