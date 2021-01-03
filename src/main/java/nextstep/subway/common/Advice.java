package nextstep.subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.exception.StationNotFoundException;

@Slf4j
@RestControllerAdvice
public class Advice {

	@ExceptionHandler(LineNotFoundException.class)
	public ResponseEntity<LineResponse> handleLineNotFoundException(LineNotFoundException e) {
		log.error("LineNotFoundException: " + e.getMessage());
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(StationNotFoundException.class)
	public ResponseEntity<StationResponse> handleStationNotFoundException(StationNotFoundException e) {
		log.error("StationNotFoundException: " + e.getMessage());
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
		log.error("RuntimeException: " + e.getMessage());
		return ResponseEntity.badRequest().build();
	}
}
