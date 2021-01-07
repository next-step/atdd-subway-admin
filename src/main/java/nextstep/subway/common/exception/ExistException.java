package nextstep.subway.common.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ExistException extends RuntimeException {
	public ExistException(String message) {
		super(message);
	}
}
