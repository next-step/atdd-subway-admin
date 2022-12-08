package nextstep.subway.consts;

public class ErrorMessage {
    public static final String ERROR_LINE_NOT_EXIST = "[ERROR] 해당 노선은 존재하지 않습니다.";
    public static final String ERROR_STATION_NOT_EXIST ="[ERROR] 해당 지하철역은 존재하지 않습니다.";
    public static final String ERROR_STATIONS_ALREADY_EXIST = "[ERROR] 상행역과 하행역이 모두 노선에 등록되어 있습니다.";
    public static final String ERROR_STATIONS_NOT_ALL = "[ERROR] 상행역과 하행역 둘 중 하나도 포함되어 있지 않습니다.";
    public static final String ERROR_DISTANCE_NOT_LONG = "[ERROR] 역 사이에 새로운 역과의 길이가 기존 역 사이 길이보다 크거나 같을 수 없습니다.";
    public static final String ERROR_NOT_REMOVE_SECTION = "[ERROR] 노선에 등록된 section이 1개면 삭제할 수 없습니다.";
    public static final String ERROR_NO_STATION_LINE = "[ERROR] 노선에 등록되어 있지 않은 역은 제거할 수 없습니다.";
}