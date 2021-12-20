package nextstep.subway.common;

import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity handleNoSuchElementException(NoSuchElementException e) {
		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity handleIllegalArgumentException(IllegalArgumentException e) {
		return ResponseEntity.badRequest().build();
	}
}
