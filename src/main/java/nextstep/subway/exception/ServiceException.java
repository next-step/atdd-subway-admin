package nextstep.subway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends RuntimeException {

    private HttpStatus status;

    private String message;

    public ServiceException(final HttpStatus status, final String message) {
        this.status = status;
        this.message = message;
    }
}
