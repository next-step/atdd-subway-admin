package nextstep.subway.advice;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SubwayControllerAdvice {

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Void> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
		return ResponseEntity.badRequest().build();
	}
}
