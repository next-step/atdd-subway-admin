package nextstep.subway;

import nextstep.subway.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(value = ApiException.class)
	public ResponseEntity<Void> apiException(ApiException exception) {
		log.error(exception.getMessage(), exception);
		return ResponseEntity.status(exception.status()).build();
	}

	@ExceptionHandler(value = EmptyResultDataAccessException.class)
	public ResponseEntity<Void> emptyResultDataAccessException(EmptyResultDataAccessException exception) {
		log.error(exception.getMessage(), exception);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Void> commonException(Exception exception) {
		log.error(exception.getMessage(), exception);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
}
