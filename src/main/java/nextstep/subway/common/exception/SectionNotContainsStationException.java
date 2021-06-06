package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class SectionNotContainsStationException extends RuntimeException {
    public SectionNotContainsStationException() {
    }

    public SectionNotContainsStationException(String message) {
        super(message);
    }

    public SectionNotContainsStationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SectionNotContainsStationException(Throwable cause) {
        super(cause);
    }

    public SectionNotContainsStationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
