package nextstep.subway;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.section.exception.InvalidDistanceException;
import nextstep.subway.section.exception.InvalidSectionRemoveException;
import nextstep.subway.section.exception.InvalidSectionException;

@RestControllerAdvice
public class SubwayControllerAdvice {

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(InvalidDistanceException.class)
	public ResponseEntity handleInvalidDistanceException(InvalidDistanceException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(InvalidSectionException.class)
	public ResponseEntity handleInvalidSectionException(InvalidSectionException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(InvalidSectionRemoveException.class)
	public ResponseEntity handleInvalidSectionRemoveException(InvalidSectionRemoveException e) {
		return ResponseEntity.badRequest().build();
	}
}
