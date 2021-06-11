package nextstep.subway.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.exception.ConflictException;
import nextstep.subway.exception.NotExistLineException;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(value = {ConflictException.class})
    @ResponseStatus(code = HttpStatus.CONFLICT)
    public ErrorMessage errorHandler(RuntimeException ex) {
        return ErrorMessage.of(ex.getMessage());
    }

    @ExceptionHandler(value = {NotExistLineException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorMessage notExistError(RuntimeException ex) {
        return ErrorMessage.of(ex.getMessage());
    }

}
