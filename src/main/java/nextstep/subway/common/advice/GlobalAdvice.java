package nextstep.subway.common.advice;

import nextstep.subway.common.domain.ErrorResponse;
import nextstep.subway.line.exception.LineException;
import nextstep.subway.station.exception.StationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalAdvice {
    @ExceptionHandler(LineException.class)
    public ResponseEntity<ErrorResponse> lineException(LineException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(StationException.class)
    public ResponseEntity<ErrorResponse> stationException(StationException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Error", e.getMessage()));
    }

}
