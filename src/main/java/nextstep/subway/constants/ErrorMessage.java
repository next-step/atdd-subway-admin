package nextstep.subway.constants;

public class ErrorMessage {

    public static final String BOTH_STATIONS_NOT_REGISTERED = "상행역과 하행역 두 역 모두 노선에 등록되어있지 않아 구간등록이 불가합니다.";
    public static final String ALREADY_REGISTERED_SECTION = "상행역과 하행역 모두 노선에 등록되어 있어 구간등록이 불가합니다.";
    public static final String SECTION_DISTANCE_NOT_VALID = "등록하려는 구간길이가 기존 등록된 역 사이 구간길이 보다 크거나 같습니다.";
    public static final String NOT_REGISTERED_LINE_STATION = "노선에 등록되어 있지 않은 역입니다.";
    public static final String LAST_LINE_STATION_CANNOT_BE_DELETED = "마지막 구간은 삭제할 수 없습니다.";
}
