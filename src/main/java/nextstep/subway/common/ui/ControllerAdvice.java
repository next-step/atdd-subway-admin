package nextstep.subway.common.ui;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.exception.CannotAddNewSectionException;
import nextstep.subway.exception.CannotRemoveSectionException;
import nextstep.subway.exception.NotFoundException;

@RestControllerAdvice
public class ControllerAdvice {

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity handleIllegalArgumentException(IllegalArgumentException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(CannotAddNewSectionException.class)
	public ResponseEntity handleCannotAddNewSectionException(CannotAddNewSectionException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(CannotRemoveSectionException.class)
	public ResponseEntity handleCannotRemoveSectionException(CannotRemoveSectionException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity handleNotFoundException(NotFoundException e) {
		return ResponseEntity.notFound().build();
	}
}
