package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionHandler {
    @ExceptionHandler(DuplicatedSectionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleSectionDuplicatedException(DuplicatedSectionException exception) {
        return exception.getMessage();
    }
}
