package nextstep.subway.exception;

public class CannotAddSectionException extends RuntimeException {

    public static final String NO_MATCHED_STATION = "상행역과 하행역 둘 중 하나 포함되어 있지 않습니다.";
    public static final String DISTANCE_LESS_THAN_ZERO = "구간의 길이는 1보다 작을 수 없습니다.";
    public static final String LONGER_THAN_SECTION = "기존 역 사이 길이보다 크거나 같을 수 없습니다.";

    public CannotAddSectionException(String message) {
        super(message);
    }
}
