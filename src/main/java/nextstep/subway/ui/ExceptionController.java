package nextstep.subway.ui;


import nextstep.subway.exception.CannotFindStationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ExceptionController {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(CannotFindStationException.class)
    public ResponseEntity cannotFoundException(CannotFindStationException e){
        log.info(e.getMessage());
        return ResponseEntity.notFound().build();
    }
}
