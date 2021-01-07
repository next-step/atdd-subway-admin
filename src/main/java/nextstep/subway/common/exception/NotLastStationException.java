package nextstep.subway.common.exception;

public class NotLastStationException extends BadRequestException {
    public NotLastStationException(String message) {
        super(message);
    }
}
