package nextstep.subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.line.dto.LineResponse;

@RestControllerAdvice
public class Advice {
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<LineResponse> handleLineRuntimeException(RuntimeException e) {
		return ResponseEntity.badRequest().build();
	}
}
