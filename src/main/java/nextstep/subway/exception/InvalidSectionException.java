package nextstep.subway.exception;

public class InvalidSectionException extends IllegalArgumentException {
    private static final String message = "구간 추가 역이 노선의 상행역과 하행역 둘 중 하나에는 포함되어 있어야 합니다.";

    public InvalidSectionException() {
        super(message);
    }
}
