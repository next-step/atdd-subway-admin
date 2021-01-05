package nextstep.subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.common.exception.NothingException;
import nextstep.subway.line.dto.LineResponse;

@Slf4j
@RestControllerAdvice
public class Advice {

	@ExceptionHandler(NothingException.class)
	public ResponseEntity<LineResponse> handleLineNotFoundException(NothingException e) {
		log.error("Target NotFoundException: " + e.getMessage());
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
		log.error("RuntimeException: " + e.getMessage());
		return ResponseEntity.badRequest().build();
	}
}
