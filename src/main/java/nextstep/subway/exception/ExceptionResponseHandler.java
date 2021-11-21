package nextstep.subway.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionResponseHandler extends ResponseEntityExceptionHandler {

	private static final String EMPTY_DESCRIPTION = "";

	@ExceptionHandler(AppException.class)
	public ResponseEntity<Object> handleAppException(AppException ex) {
		ExceptionResponse response = new ExceptionResponse(ex);
		return ResponseEntity
			.status(response.getHttpStatus())
			.body(new ExceptionResponse(ex));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		ExceptionResponse response = new ExceptionResponse(
			httpStatus, ex.getMessage(), EMPTY_DESCRIPTION, LocalDateTime.now());
		return ResponseEntity.status(httpStatus)
			.body(response);
	}
}
