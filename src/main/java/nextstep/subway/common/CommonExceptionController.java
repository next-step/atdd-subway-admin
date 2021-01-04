package nextstep.subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionController {

	@ExceptionHandler(Exception.class)
	public ResponseEntity handleIllegalArgumentException(Exception e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}
}
