package nextstep.subway.line.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CanNotDeleteSectionException extends RuntimeException {

    public CanNotDeleteSectionException(String message) {
        super(message);
    }
}
