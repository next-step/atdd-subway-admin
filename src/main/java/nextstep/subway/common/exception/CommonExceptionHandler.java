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

	@ExceptionHandler(IllegalDistanceException.class)
	protected ResponseEntity illegalDistanceException(IllegalDistanceException exception) {
		return ResponseEntity.badRequest().body(new ErrorResponse(exception));
	}

	@ExceptionHandler(DuplicateAllStationException.class)
	protected ResponseEntity duplicateAllStationException(DuplicateAllStationException exception) {
		return ResponseEntity.badRequest().body(new ErrorResponse(exception));
	}

	@ExceptionHandler(NotExistAllStationException.class)
	protected ResponseEntity notExistAllStationException(NotExistAllStationException exception) {
		return ResponseEntity.badRequest().body(new ErrorResponse(exception));
	}

	@ExceptionHandler(OneSectionCannotRemoveException.class)
	protected ResponseEntity oneSectionCannotRemoveException(OneSectionCannotRemoveException exception) {
		return ResponseEntity.badRequest().body(new ErrorResponse(exception));
	}

}
