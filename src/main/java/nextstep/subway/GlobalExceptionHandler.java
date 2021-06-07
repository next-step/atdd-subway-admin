package nextstep.subway;

import nextstep.subway.exception.ApiException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = ApiException.class)
	public ResponseEntity<Void> apiException(ApiException exception) {
		return ResponseEntity.status(exception.status()).build();
	}

	@ExceptionHandler(value = EmptyResultDataAccessException.class)
	public ResponseEntity<Void> emptyResultDataAccessException(EmptyResultDataAccessException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Void> commonException(Exception exception) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
}
