package nextstep.subway.common.advice;

import nextstep.subway.common.exception.DuplicateParameterException;
import nextstep.subway.common.exception.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * packageName : nextstep.subway.common.advice
 * fileName : CommonAdvice
 * author : haedoang
 * date : 2021-11-17
 * description : 공통 에러 핸들링 어드바이스
 */
@RestControllerAdvice
public class CommonAdvice {
    @ExceptionHandler({
            DataIntegrityViolationException.class,
            NotFoundException.class,
            DuplicateParameterException.class})
    public ResponseEntity handleIllegalArgsException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
