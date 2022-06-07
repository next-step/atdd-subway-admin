package nextstep.subway.common.exception;

import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.section.exception.SectionDuplicationException;
import nextstep.subway.station.exception.StationAllNotExistedException;
import nextstep.subway.station.exception.StationNotFoundException;
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StationNotFoundException.class)
    public ErrorResponse handleStationNotFoundException() {
        return ErrorResponse.of(ErrorMessage.STATION_NOT_FOUND.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StationAllNotExistedException.class)
    public ErrorResponse handleStationAllNotExistedException() {
        return ErrorResponse.of(ErrorMessage.STATION_ALL_NOT_EXISTED_EXCEPTION.getMessage());
    }
}
