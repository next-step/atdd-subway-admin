package nextstep.subway.line.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.common.exception.BaseException;

@RestControllerAdvice
public class LineControllerAdvice {
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<String> errorHandler(BaseException e) {
		return new ResponseEntity<>(e.getMessage(), e.getStatus());
	}
}
