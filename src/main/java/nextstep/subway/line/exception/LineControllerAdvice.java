package nextstep.subway.line.exception;

import nextstep.subway.common.ErrorCode;
import nextstep.subway.common.ErrorResponse;
import nextstep.subway.section.exception.ExistSameStationsException;
import nextstep.subway.section.exception.NotExistAnySameStationException;
import nextstep.subway.section.exception.NotUnderSectionDistanceException;
import nextstep.subway.station.exception.NotFoundStationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LineControllerAdvice {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundLineException.class)
    public ErrorResponse handleNotFoundException() {
        return new ErrorResponse(ErrorCode.NOT_FOUND_LINE_MESSAGE.getCode(), ErrorCode.NOT_FOUND_LINE_MESSAGE.getDescription());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotUnderSectionDistanceException.class)
    public ErrorResponse handleNotAddSectionException() {
        return new ErrorResponse(ErrorCode.NOT_UNDER_SECTION_DISTANCE_MESSAGE.getCode(), ErrorCode.NOT_UNDER_SECTION_DISTANCE_MESSAGE.getDescription());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ExistSameStationsException.class)
    public ErrorResponse handleExistSameStationsException() {
        return new ErrorResponse(ErrorCode.EXIST_SAME_STATIONS_MESSAGE.getCode(), ErrorCode.EXIST_SAME_STATIONS_MESSAGE.getDescription());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotExistAnySameStationException.class)
    public ErrorResponse handleNotExistAnySameStationException() {
        return new ErrorResponse(ErrorCode.NOT_EXIST_ANY_SAME_STATION_MESSAGE.getCode(), ErrorCode.NOT_EXIST_ANY_SAME_STATION_MESSAGE.getDescription());
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundStationException.class)
    public ErrorResponse handleNotFoundStationException() {
        return new ErrorResponse(ErrorCode.NOT_FOUND_STATION_MESSAGE.getCode(), ErrorCode.NOT_FOUND_STATION_MESSAGE.getDescription());
    }
}
