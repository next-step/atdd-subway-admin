package nextstep.subway.section.exception;

public class BelowZeroDistanceException extends RuntimeException{
    public BelowZeroDistanceException() {
        super("거리는 0이하가 될 수 없습니다.");
    }
}
