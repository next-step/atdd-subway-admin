package nextstep.subway.exception;

public class NotFoundLine extends RuntimeException {
    public NotFoundLine(Long id) {
        super(String.format("[Line id : %d] not found", id));
    }
}
