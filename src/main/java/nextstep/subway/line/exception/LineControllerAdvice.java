package nextstep.subway.line.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LineControllerAdvice {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundLineException.class)
    public ErrorResponse handleException() {
        return new ErrorResponse(ErrorCode.NOT_FOUND_LINE_MESSAGE.getCode(), ErrorCode.NOT_FOUND_LINE_MESSAGE.getDescription());
    }
}
