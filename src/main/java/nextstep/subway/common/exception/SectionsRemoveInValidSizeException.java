package nextstep.subway.common.exception;

public class SectionsRemoveInValidSizeException extends IllegalArgumentException {

	public static final String INVALID_REMOVE_SIZE_MESSAGE = "구간이 1개이하인 경우, 삭제할 수 없습니다.";

	public SectionsRemoveInValidSizeException() {
		super(INVALID_REMOVE_SIZE_MESSAGE);
	}
}
