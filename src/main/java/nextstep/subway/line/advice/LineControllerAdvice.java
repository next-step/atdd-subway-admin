package nextstep.subway.line.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.line.exception.ResourceAlreadyExistException;
import nextstep.subway.line.ui.LineController;

@RestControllerAdvice(assignableTypes = LineController.class)
public class LineControllerAdvice {

	@ExceptionHandler(ResourceAlreadyExistException.class)
	public ResponseEntity<String> errorHandler(Exception e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
	}
}
