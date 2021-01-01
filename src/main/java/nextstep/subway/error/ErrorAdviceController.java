package nextstep.subway.error;

import java.net.BindException;
import nextstep.subway.line.exception.AlreadySavedLineException;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.exception.StationNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author : leesangbae
 * @project : subway
 * @since : 2020-12-24
 */
@RestControllerAdvice
public class ErrorAdviceController {

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleBindException(Exception e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }


    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity<?> handleLineNotFoundException(LineNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(AlreadySavedLineException.class)
    public ResponseEntity<?> handleAlreadySavedLineException(AlreadySavedLineException exception) {
        return ResponseEntity.badRequest().build();
    }


    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity<?> handleStationNotFoundException(StationNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }


}
