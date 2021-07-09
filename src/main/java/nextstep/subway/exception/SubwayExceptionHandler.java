package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class SubwayExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity handleException(IllegalArgumentException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
	}

	@ExceptionHandler(InvalidSectionException.class)
	public ResponseEntity handleInvalidSectionException(InvalidSectionException e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}

	@ExceptionHandler(DeleteSectionException.class)
	public ResponseEntity handleDeleteSectionException(DeleteSectionException e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}

	@ExceptionHandler(NotFoundLineException.class)
	public ResponseEntity handleNotFoundLineException(NotFoundLineException e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}

	@ExceptionHandler(NotFoundStationException.class)
	public ResponseEntity handleNotFoundStationException(NotFoundStationException e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
}
