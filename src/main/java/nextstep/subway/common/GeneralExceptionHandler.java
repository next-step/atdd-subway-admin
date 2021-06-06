package nextstep.subway.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import nextstep.subway.line.exception.DuplicateLineException;
import nextstep.subway.line.exception.NoSuchLineException;

@ControllerAdvice
public class GeneralExceptionHandler {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<?> handleNotFoundException(Exception e) {
		log.warn("핸들링 되지 않은 예외입니다.", e);
		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler(DuplicateLineException.class)
	public ResponseEntity<?> duplicateException(Exception e) {
		log.debug("중복된 값이 입력되었습니다.", e);
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(NoSuchLineException.class)
	public ResponseEntity<?> noSuchResourceException(Exception e) {
		log.debug("존재하지 않는 값이 요청되었습니다.", e);
		return ResponseEntity.notFound().build();
	}
}
