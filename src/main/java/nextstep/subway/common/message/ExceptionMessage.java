package nextstep.subway.common.message;

public class ExceptionMessage {
    public static final String REQUIRED = "필수값은 비어선 안됩니다!";
    public static final String NOT_FOUND_LINE = "노선을 찾을 수 없습니다.";
    public static final String NOT_FOUND_STATION = "지하철역을 찾을 수 없습니다.";
    public static final String UP_STATION_EQUALS_DOWN_STATION = "상행역과 하행역은 같을 수 없습니다.";
    public static final String INVALID_SECTION_DISTANCE = "지하철 구간의 길이는 0보다 큰 값이어야 합니다. (현재 = %d)";
    public static final String ALREADY_ADDED_SECTION = "이미 등록된 지하철 구간입니다.";
    public static final String NOT_INCLUDE_UP_STATION_AND_DOWN_STATION = "상행/하행 종점역 둘 다 포함하고 있지 않습니다.";
    public static final String UP_STATION_NOT_EXIST_IN_LINE = "지하철 노선에 상행종점역이 존재하지 않습니다.";

    private ExceptionMessage() {
    }
}
