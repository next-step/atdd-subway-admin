package nextstep.subway.common;

import nextstep.subway.line.application.NoLineException;
import nextstep.subway.section.application.AlreadyExistsException;
import nextstep.subway.section.application.ExceedDistanceException;
import nextstep.subway.section.application.NoMatchStationException;
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
        logger.error("구간의 길이가 기존 구간보다 깁니다. {}", e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity handleAlreadyExistsException(AlreadyExistsException e) {
        logger.error("이미 동일한 구간이 존재합니다. section: {}", e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NoMatchStationException.class)
    public ResponseEntity handleNoMatchStationException(NoMatchStationException e) {
        logger.error("등록하려는 구간의 상행역과 하행역이 존재하지 않습니다. section: {}", e.getMessage());
        return ResponseEntity.badRequest().build();
    }
}
