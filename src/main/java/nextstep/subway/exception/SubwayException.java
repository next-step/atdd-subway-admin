package nextstep.subway.exception;

public class SubwayException extends RuntimeException {
    public SubwayException(final SubwayExceptionMessage message) {
        super(message.getMessage());
    }
}
