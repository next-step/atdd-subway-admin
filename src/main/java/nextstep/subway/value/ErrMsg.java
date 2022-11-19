package nextstep.subway.value;

public class ErrMsg {
    ErrMsg() {
        throw new AssertionError();
    }
    private static final String CANNOT_FIND_LINE = "번 노선을 찾을 수 없습니다.";
    private static final String CANNOT_FIND_STATION = "번 역을 찾을 수 없습니다.";
    private static final String CANNOT_FIND_SECTION = "번 구간을 찾을 수 없습니다.";

    public static final String SECTION_ALREADY_EXISTS = "이미 해당 구간이 존재합니다.";
    public static final String NO_MATCHING_STATIONS = "구간을 추가하기위한 상행역과 하행역이 존재하지 않습니다.";
    public static final String DISTANCE_TOO_LONG = "주어진 구간의 거리가 추가 가능한 구간의 길이보다 깁니다.";

    public static final String INAPPROPRIATE_DISTANCE = "구간의 길이는 양수여야 합니다.";

    public static String notFoundLine(Long id) {
        return id+CANNOT_FIND_LINE;
    }

    public static String notFoundStation(Long id) {
        return id+CANNOT_FIND_STATION;
    }
    public static String notFoundSection(Long id) {
        return id+CANNOT_FIND_SECTION;
    }
}
