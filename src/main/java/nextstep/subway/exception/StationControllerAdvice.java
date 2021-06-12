package nextstep.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.exception.dto.ErrorResponse;
import nextstep.subway.exception.station.StationsAlreadyExistException;
import nextstep.subway.exception.station.StationsNoExistException;

@RestControllerAdvice
public class StationControllerAdvice extends ControllerAdvice {

    @ExceptionHandler(StationsAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> stationsAlreadyExistException(StationsAlreadyExistException e) {
        return getBadResponse(e.getMessage());
    }

    @ExceptionHandler(StationsNoExistException.class)
    public ResponseEntity<ErrorResponse> stationsNoExistException(StationsNoExistException e) {
        return getBadResponse(e.getMessage());
    }
}
