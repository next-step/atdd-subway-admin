package nextstep.subway;

import nextstep.subway.exception.ApiException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
public class ApiExceptionHandler {

	@ExceptionHandler(value = ApiException.class)
	public ResponseEntity apiException(ApiException exception) {
		return ResponseEntity.status(exception.status())
							 .build();
	}

	@ExceptionHandler(value = EmptyResultDataAccessException.class)
	public ResponseEntity EmptyResultDataAccessException(EmptyResultDataAccessException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
							 .build();
	}
}
