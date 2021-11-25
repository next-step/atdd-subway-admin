package nextstep.subway.exception;

public class NotIncludeStation extends BadRequestException {
    private static final String NOT_INCLUDE_MESSAGE = "노선에 등록되지 않은 역은 제거 할 수 없습니다.";

    public NotIncludeStation() {
        super(NOT_INCLUDE_MESSAGE);
    }
}
