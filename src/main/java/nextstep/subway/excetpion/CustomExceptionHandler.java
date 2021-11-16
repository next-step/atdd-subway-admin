package nextstep.subway.excetpion;

import nextstep.subway.line.exception.DistanceLengthException;
import nextstep.subway.line.exception.DuplicateLineStationException;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.exception.StationNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("nextstep.subway")
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(ErrorCode.DEFAULT_ERROR.getHttpStatus())
                .body(ErrorCode.DEFAULT_ERROR.getMessage());
    }

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity handleLineNotFoundException(LineNotFoundException e) {
        e.printStackTrace();
        ErrorResponse response = ErrorResponse.build()
                .httpStatus(e.getErrorCode().getHttpStatus())
                .message(e.getMessage());
        return new ResponseEntity(response, response.getHttpStatus());
    }

    @ExceptionHandler(DuplicateLineStationException.class)
    public ResponseEntity handleDuplicateLineStationException(DuplicateLineStationException e) {
        e.printStackTrace();
        ErrorResponse response = ErrorResponse.build()
                .httpStatus(e.getErrorCode().getHttpStatus())
                .message(e.getMessage());
        return new ResponseEntity(response, response.getHttpStatus());
    }

    @ExceptionHandler(DistanceLengthException.class)
    public ResponseEntity handleDistanceLengthException(DistanceLengthException e) {
        e.printStackTrace();
        ErrorResponse response = ErrorResponse.build()
                .httpStatus(e.getErrorCode().getHttpStatus())
                .message(e.getMessage());
        return new ResponseEntity(response, response.getHttpStatus());
    }

    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity handleStationNotFoundException(StationNotFoundException e) {
        e.printStackTrace();
        ErrorResponse response = ErrorResponse.build()
                .httpStatus(e.getErrorCode().getHttpStatus())
                .message(e.getMessage());
        return new ResponseEntity(response, response.getHttpStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

}
