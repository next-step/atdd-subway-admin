package nextstep.subway.exception;

public class InvalidRemoveSectionException extends IllegalArgumentException {
    private static final String message = "구간이 하나인 노선은 제거할 수 없습니다.";

    public InvalidRemoveSectionException() {
        super(message);
    }
}
