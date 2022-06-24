package nextstep.subway.error.exception;

import nextstep.subway.error.SubwayError;

public class SubWayApiException extends RuntimeException {
    private final SubwayError error;

    public SubWayApiException(SubwayError error) {
        this.error = error;
    }

    public SubWayApiException(SubwayError error, String message) {
        super(message);
        this.error = error;
    }

    public SubwayError getError() {
        return error;
    }
}
