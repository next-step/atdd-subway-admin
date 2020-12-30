package nextstep.subway.advice.exception;

public class LineNotFoundException extends RuntimeException {

    public LineNotFoundException(String message) {
        super(message);
    }

    public LineNotFoundException(Long id) {
        super("존재하는 노선이 없습니다 (id : " +id+" )");
    }
}
