package nextstep.subway.common.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class EntityNotExistException extends RuntimeException{
    public EntityNotExistException() {
    }

    public EntityNotExistException(String message) {
        super(message);
    }

    public EntityNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotExistException(Throwable cause) {
        super(cause);
    }

    public EntityNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
