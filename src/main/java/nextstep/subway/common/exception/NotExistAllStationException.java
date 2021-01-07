package nextstep.subway.common.exception;

/**
 * @author : byungkyu
 * @date : 2020/12/29
 * @description :
 **/
public class NotExistAllStationException extends RuntimeException implements CommonException {
	public static final String ERROR_CODE = "NOT_EXIST_ALL_STATION_EXCEPTION";

	private String errorMessage = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.";

	public NotExistAllStationException() {
		super();
	}

	public NotExistAllStationException(String errorMessage) {
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
