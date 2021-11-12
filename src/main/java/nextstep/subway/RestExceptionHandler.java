package nextstep.subway;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.line.exception.LineNotFoundException;

@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(LineNotFoundException.class)
	public ResponseEntity<Void> handleNotFoundException(LineNotFoundException e) {
		return ResponseEntity.notFound().build();
	}
}
