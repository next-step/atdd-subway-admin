package nextstep.subway.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.common.dto.ErrorResponse;

/**
 * @author : byungkyu
 * @date : 2020/12/29
 * @description :
 **/
@RestControllerAdvice
public class CommonExceptionHandler {
	@ExceptionHandler(NotFoundException.class)
	protected ResponseEntity notFoundException(NotFoundException exception) {
		return ResponseEntity.badRequest().body(new ErrorResponse(exception));
	}
}
