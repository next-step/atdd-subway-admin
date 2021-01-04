package nextstep.subway.common;

import nextstep.subway.line.application.NoLineException;
import nextstep.subway.section.application.ExceedDistanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor {
    Logger logger = LoggerFactory.getLogger(ControllerAdvisor.class);

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NoLineException.class)
    public ResponseEntity handleNoLineException(NoLineException e) {
        logger.error("해당 노선이 존재 하지 않습니다. 노선 ID: {}", e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(ExceedDistanceException.class)
    public ResponseEntity handleExceedDistanceException(ExceedDistanceException e) {
        logger.error("구간의 길이가 기존 구간보다 깁니다");
        return ResponseEntity.badRequest().build();
    }
}
