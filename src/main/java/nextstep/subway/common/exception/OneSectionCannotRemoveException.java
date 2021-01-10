package nextstep.subway.common.exception;

/**
 * @author : byungkyu
 * @date : 2020/12/29
 * @description :
 **/
public class OneSectionCannotRemoveException extends RuntimeException implements CommonException {
	public static final String ERROR_CODE = "ONE_SECTION_CANNOT_REMOTE_EXCEPTION";

	private String errorMessage = "하나의 노선이 존재할 경우 역을 제거할 수 없습니다..";

	public OneSectionCannotRemoveException() {
		super();
	}

	public OneSectionCannotRemoveException(String errorMessage) {
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
