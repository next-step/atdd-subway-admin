package nextstep.subway.ui.config;


import lombok.extern.slf4j.Slf4j;
import nextstep.subway.exception.CannotFindException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
//@Slf4j
class ExceptionController {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    // CannotFindException 500 -> 404
    @ExceptionHandler(CannotFindException.class)
    public ResponseEntity cannotFoundException(CannotFindException e){
        log.info(e.getMessage());
        return ResponseEntity.notFound().build();
    }
}
