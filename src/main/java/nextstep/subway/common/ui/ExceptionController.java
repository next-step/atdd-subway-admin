package nextstep.subway.common.ui;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.common.exception.SubwayException;

@RestControllerAdvice
public class ExceptionController {
    private static final Logger log = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> noSuchElementExceptionHandler(NoSuchElementException e) {
        log.error("요청받은 데이터가 존재하지 않습니다.", e);
        return ResponseEntity.notFound()
            .build();
    }

    @ExceptionHandler({DataIntegrityViolationException.class, SubwayException.class})
    public ResponseEntity<Void> badRequestExceptionHandler(Exception e) {
        log.error("잘못된 요청입니다.", e);

        return ResponseEntity.badRequest()
            .build();
    }
}
