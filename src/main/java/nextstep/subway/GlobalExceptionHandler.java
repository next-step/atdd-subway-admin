package nextstep.subway;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.line.exception.LineNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException exception) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(LineNotFoundException.class)
	public ResponseEntity<Void> handleLineNotFoundException(LineNotFoundException exception) {
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Void> handleException(BadRequestException exception) {
		return ResponseEntity.badRequest().build();
	}
}
