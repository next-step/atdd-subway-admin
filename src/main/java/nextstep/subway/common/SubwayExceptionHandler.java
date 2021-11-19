package nextstep.subway.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SubwayExceptionHandler {

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity handleDataIntegrityViolationException(DataIntegrityViolationException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity handleIllegalArgsException(IllegalArgumentException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity handleIllegalStateException(IllegalStateException e) {
		return ResponseEntity.badRequest().build();
	}

}
