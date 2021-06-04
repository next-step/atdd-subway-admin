package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class SectionDoesNotDeleteStationException extends RuntimeException {
    public SectionDoesNotDeleteStationException() {
    }

    public SectionDoesNotDeleteStationException(String message) {
        super(message);
    }

    public SectionDoesNotDeleteStationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SectionDoesNotDeleteStationException(Throwable cause) {
        super(cause);
    }

    public SectionDoesNotDeleteStationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
