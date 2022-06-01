package nextstep.subway.consts;

import java.util.Locale;

public class ErrorMessage {
    public static final String ERROR_LINE_NOT_FOUND = "[ERROR] 해당 노선이 존재하지 않습니다: %d";
    public static final String ERROR_STATION_NOT_FOUND = "[ERROR] 해당 지하철역이 존재하지 않습니다: %d";
    public static final String ERROR_DISTANCE_TOO_SMALL = "[ERROR] Distance는 %d미만이 될 수 없습니다.";
    public static final String ERROR_SECTION_ALREADY_REGISTERED_STATIONS = "[ERROR] 노선에 이미 존재하는 구간입니다: [%s, %s]";
    public static final String ERROR_SECTION_UNKNOWN_STATIONS = "[ERROR] 노선에 존재하지 않는 역으로 이루어진 구간입니다: [%s, %s]";
}
