package nextstep.subway.common.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NothingException extends RuntimeException {
	public NothingException(String message) {
		super(message);
	}
}
