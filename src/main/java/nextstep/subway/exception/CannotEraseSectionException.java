package nextstep.subway.exception;

public class CannotEraseSectionException extends RuntimeException {
    private static final String MESSAGE = "해당 노선은 제거할 수 없습니다.";

    public CannotEraseSectionException() {
        super(MESSAGE);
    }
}
