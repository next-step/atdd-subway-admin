package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(DistanceException.class)
	protected ResponseEntity<ErrorResponse> handleException(DistanceException e) {
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(ErrorCode.DISTANCE_RANGE_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(AlreadyExistsStationException.class)
	protected ResponseEntity<ErrorResponse> handleException(AlreadyExistsStationException e) {
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(ErrorCode.ALREADY_EXISTS_STATION_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NotIncludeLineBothStationException.class)
	protected ResponseEntity<ErrorResponse> handleException(NotIncludeLineBothStationException e) {
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(ErrorCode.NOT_INCLUDE_LINE_BOTH_STATION_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
