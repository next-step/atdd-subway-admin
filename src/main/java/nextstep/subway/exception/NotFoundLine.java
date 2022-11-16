package nextstep.subway.exception;

public class NotFoundLine extends RuntimeException {
    public NotFoundLine() {
        super("not found line");
    }
}
