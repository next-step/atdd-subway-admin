package nextstep.subway.exception;

public class CannotAddSectionException extends BadRequestException {

    public static final String NO_MATCHED_STATION = "상행역과 하행역 둘 중 하나 포함되어 있지 않습니다.";
    public static final String LONGER_THAN_SECTION = "기존 역 사이 길이보다 크거나 같을 수 없습니다.";
    public static final String UP_AND_DOWN_STATION_ALL_EXISTS = "상행역과 하행역 모두 노선에 존재합니다";

    public CannotAddSectionException(String message) {
        super(message);
    }
}
