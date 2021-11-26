package nextstep.subway.line.exception;

public class LineAlreadyBothRegisteredException extends IllegalArgumentException {
    private static final String DEFAULT_MESSAGE = "상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.";

    public LineAlreadyBothRegisteredException() {
        super(DEFAULT_MESSAGE);
    }
}
