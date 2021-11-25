package nextstep.subway.line.exception;

public class NotFoundLineException extends RuntimeException {
    public NotFoundLineException(Long id) {
        super(id + " 지하철역이 없습니다");
    }
}
