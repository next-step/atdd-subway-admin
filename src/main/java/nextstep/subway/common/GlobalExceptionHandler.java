package nextstep.subway.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import nextstep.subway.common.exception.DuplicateSectionException;
import nextstep.subway.common.exception.InvalidDistanceException;
import nextstep.subway.common.exception.NoDataException;
import nextstep.subway.common.exception.NotMatchStationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(RuntimeException.class)
	public BusinessError handleIllegalArgumentException(RuntimeException e) {
		return BusinessError.of(e.getMessage());
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoDataException.class)
	public BusinessError handleNoDataException(NoDataException e) {
		return BusinessError.of(e.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(DuplicateSectionException.class)
	public BusinessError handleDuplicateSectionException(DuplicateSectionException e) {
		return BusinessError.of(e.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(InvalidDistanceException.class)
	public BusinessError handleInvalidDistanceException(InvalidDistanceException e) {
		return BusinessError.of(e.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NotMatchStationException.class)
	public BusinessError handleNotMatchStationException(NotMatchStationException e) {
		return BusinessError.of(e.getMessage());
	}
}
