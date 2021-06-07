package nextstep.subway.common.error;

import nextstep.subway.line.application.exception.LineDuplicatedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(LineDuplicatedException.class)
    public ErrorResponse handleLineDuplicatedException(LineDuplicatedException e) {
        return ErrorResponse.of(e.getMessage());
    }
}
