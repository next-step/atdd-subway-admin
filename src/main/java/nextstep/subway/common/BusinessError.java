package nextstep.subway.common;

public class BusinessError {

	private String message;

	public BusinessError() {
		this.message = "";
	}
	public BusinessError(String message) {
		this.message = message;
	}

	public static BusinessError of(String message) {
		return new BusinessError(message);
	}

	public String getMessage() {
		return message;
	}
}
