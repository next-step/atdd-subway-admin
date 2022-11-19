package nextstep.subway.line.exception;

public class NoRelationStationException extends RuntimeException {

    public NoRelationStationException() {
        super("근접한 상행선과 하행선이 없습니다.");
    }
}
