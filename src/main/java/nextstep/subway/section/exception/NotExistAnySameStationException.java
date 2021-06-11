package nextstep.subway.section.exception;

public class NotExistAnySameStationException extends RuntimeException {
    public NotExistAnySameStationException() {
    }

    public NotExistAnySameStationException(String message) {
        super(message);
    }
}
