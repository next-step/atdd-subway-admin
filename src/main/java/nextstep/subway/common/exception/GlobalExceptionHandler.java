package nextstep.subway.common.exception;

import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.section.exception.SectionDuplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(LineNotFoundException.class)
    public ErrorResponse handleLineNotFoundException() {
        return ErrorResponse.of(ErrorMessage.LINE_NOT_FOUND.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SectionDuplicationException.class)
    public ErrorResponse handleSectionDuplicationException() {
        return ErrorResponse.of(ErrorMessage.SECTION_DUPLICATION.getMessage());
    }
}
