package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidSectionException extends RuntimeException {
    public InvalidSectionException(String message) {
        super(message);
    }
}
