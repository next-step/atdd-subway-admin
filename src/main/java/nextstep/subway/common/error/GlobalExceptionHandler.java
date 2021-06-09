package nextstep.subway.common.error;

import nextstep.subway.line.application.exception.LineDuplicatedException;
import nextstep.subway.line.application.exception.LineNotFoundException;
import nextstep.subway.station.application.StationNotFoundException;
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

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(LineNotFoundException.class)
    public ErrorResponse handleLineNotFoundException(LineNotFoundException e) {
        return ErrorResponse.of(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(StationNotFoundException.class)
    public ErrorResponse handleStationNotFoundException(StationNotFoundException e) {
        return ErrorResponse.of(e.getMessage());
    }
}
