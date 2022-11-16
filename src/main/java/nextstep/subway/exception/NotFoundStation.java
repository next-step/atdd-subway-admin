package nextstep.subway.exception;

public class NotFoundStation extends RuntimeException {
    public NotFoundStation() {
        super("not found station");
    }
}
