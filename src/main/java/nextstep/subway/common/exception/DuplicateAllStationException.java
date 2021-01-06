package nextstep.subway.common.exception;

/**
 * @author : byungkyu
 * @date : 2021/01/07
 * @description :
 **/
public class DuplicateAllStationException extends RuntimeException implements CommonException {
	public static final String ERROR_CODE = "DUPLICATE_ALL_STATION_EXCEPTION";

	private String errorMessage = "상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.";

	public DuplicateAllStationException() {
		super();
	}

	public DuplicateAllStationException(String errorMessage) {
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

