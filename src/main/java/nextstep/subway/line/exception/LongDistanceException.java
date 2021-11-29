package nextstep.subway.line.exception;

public class LongDistanceException extends RuntimeException {
    public LongDistanceException() {
        super("새 노선의 길이가 기존 노선의 길이보다 작아야 합니다.");
    }
}
