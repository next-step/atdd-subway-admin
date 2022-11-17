package nextstep.subway.exception;

public class NotFoundStation extends RuntimeException {
    public NotFoundStation(Long id) {
        super(String.format("[Station id : %d] not found", id));
    }
}
