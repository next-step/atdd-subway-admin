package nextstep.subway.common.exception;

/**
 * @author : byungkyu
 * @date : 2020/12/29
 * @description :
 **/
public class NotFoundException extends RuntimeException implements CommonException{
	public static final String ERROR_CODE = "NOT_FOUND_EXCEPTION";

	private String errorMessage;

	public NotFoundException() {
		super();
	}

	public NotFoundException(String errorMessage) {
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
