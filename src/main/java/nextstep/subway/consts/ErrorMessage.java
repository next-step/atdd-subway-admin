package nextstep.subway.consts;

import java.util.Locale;

public class ErrorMessage {
    public static final String ERROR_LINE_NOT_FOUND = "[ERROR] 해당 노선이 존재하지 않습니다: %d";
    public static final String ERROR_STATION_NOT_FOUND = "[ERROR] 해당 지하철역이 존재하지 않습니다: %d";
    public static final String ERROR_DISTANCE_TOO_SMALL = "[ERROR] Distance는 %d미만이 될 수 없습니다.";
    public static final String ERROR_SECTION_ALREADY_REGISTERED_STATIONS = "[ERROR] 노선에 이미 존재하는 구간입니다: [%s, %s]";
    public static final String ERROR_SECTION_UNKNOWN_STATIONS = "[ERROR] 노선에 존재하지 않는 역으로 이루어진 구간입니다: [%s, %s]";
    public static final String ERROR_LINENAME_EMPTY = "[ERROR] 노선명은 빈값일 수 없습니다.";
    public static final String ERROR_LINECOLOR_EMPTY = "[ERROR] 노선색은 빈값일 수 없습니다.";
    public static final String ERROR_STATIONNAME_EMPTY = "[ERROR] 지하철역명은 빈값일 수 없습니다.";
    public static final String ERROR_CANNOT_DELETE_SECTION_MINIMUM_LENGTH = "[ERROR] 구간이 %d개 이하일 때는 제거할 수 없습니다.";
    public static final String ERROR_PREVIOUS_SECTION_NOT_FOUND = "[ERROR] Station %s의 이전 구간을 찾을 수 없습니다.";
    public static final String ERROR_POST_SECTION_NOT_FOUND = "[ERROR] Station %s의 다음 구간을 찾을 수 없습니다.";
    public static final String ERROR_END_SECTION_NOT_FOUND = "[ERROR] Station %s의 종점 구간을 찾을 수 없습니다.";
    public static final String ERROR_CANNOT_DELETE_SECTION_NOT_EXIST = "[ERROR] 노선에 존재하지 않는 Station %s은 제거할 수 없습니다.";
}
