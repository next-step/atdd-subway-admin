package nextstep.subway.common.exception;

/**
 * @author : byungkyu
 * @date : 2021/01/07
 * @description :
 **/
public class IllegalDistanceException extends RuntimeException implements CommonException {
	public static final String ERROR_CODE = "ILLEGAL_DISTANCE_EXCEPTION";

	private String errorMessage = "기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.";

	public IllegalDistanceException() {
		super();
	}

	public IllegalDistanceException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	@Override
	public String getErrorCode() {
		return ERROR_CODE;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
}

