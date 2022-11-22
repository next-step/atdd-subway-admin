package nextstep.subway.exception;

public class NotFoundStationException extends RuntimeException {
    public NotFoundStationException(Long id) {
        super(String.format("[Station id : %d] not found", id));
    }

    public NotFoundStationException() {
    }
}
