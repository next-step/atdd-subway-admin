package nextstep.subway.common.handler;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.common.dto.ErrorResponse;
import nextstep.subway.exception.CanNotDeleteStateException;
import nextstep.subway.exception.LimitDistanceException;
import nextstep.subway.exception.CanNotAddSectionException;
import nextstep.subway.exception.RegisteredSectionException;
import nextstep.subway.line.ui.LineController;

@RestControllerAdvice(basePackageClasses = LineController.class)
public class SectionExceptionHandler {

	@ExceptionHandler(LimitDistanceException.class)
	public ResponseEntity<ErrorResponse> handleLimitDistanceException(LimitDistanceException e) {
		return ResponseEntity.badRequest().body(ErrorResponse.of(BAD_REQUEST.value(), e.getMessage()));
	}

	@ExceptionHandler(RegisteredSectionException.class)
	public ResponseEntity<ErrorResponse> handleRegisteredSectionException(RegisteredSectionException e) {
		return ResponseEntity.badRequest().body(ErrorResponse.of(BAD_REQUEST.value(), RegisteredSectionException.MESSAGE));
	}

	@ExceptionHandler(CanNotAddSectionException.class)
	public ResponseEntity<ErrorResponse> handleCanNotAddSectionException(CanNotAddSectionException e) {
		return ResponseEntity.badRequest().body(ErrorResponse.of(BAD_REQUEST.value(), CanNotAddSectionException.MESSAGE));
	}

	@ExceptionHandler(CanNotDeleteStateException.class)
	public ResponseEntity<ErrorResponse> handleCanNotDeleteStateException(CanNotDeleteStateException e) {
		return ResponseEntity.badRequest().body(ErrorResponse.of(BAD_REQUEST.value(), CanNotDeleteStateException.MESSAGE));
	}

}
