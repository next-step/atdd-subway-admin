package nextstep.subway.ui;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityExistsException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ControllerExceptionHandler {

    public static final String ERROR = "ERROR";

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public ErrorResult noSuchExHandler(NoSuchElementException e) {
        return new ErrorResult(ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityExistsException.class)
    public ErrorResult handleIllegalArgsException(EntityExistsException e) {
        return new ErrorResult(ERROR, e.getMessage());
    }

    static class ErrorResult {
        private final String code;
        private final String message;

        public ErrorResult(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
