package nextstep.subway.exception.handler;

import nextstep.subway.exception.DupSectionException;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiHandler {

    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity StationNotfoundExceptionHandler(StationNotFoundException e){
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity LineNotfoundExceptionHandler(LineNotFoundException e){
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity EmptyResultDataAccessExceptionHandler(EmptyResultDataAccessException e){
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(DupSectionException.class)
    public ResponseEntity DupSectionExceptionHandler(DupSectionException e){
        return ResponseEntity.badRequest()
            .build();
    }
}
