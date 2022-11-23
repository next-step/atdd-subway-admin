package nextstep.subway.constant;

public class Message {
    public final static String NOT_FOUND_STATION_ERR = "해당 지하철역을 찾을 수 없습니다.";
    public final static String NOT_FOUND_LINE_ERR = "해당 노선을 찾을 수 없습니다.";
    public final static String NOT_FOUND_SECTION_ERR = "해당 구간을 찾을 수 없습니다.";
    public final static String NOT_VALID_REMOVE_ONLY_ONE_SECTION = "구간이 하나인 노선은 제거 할 수 없습니다.";
    public final static String NOT_CONTAIN_STATION_IN_LINE = "해당 구간에 포함된 역이 아닙니다.";


    public final static String NOT_VALID_SECTION_DISTANCE = "기존 역 사이 길이보다 크거나 같은 구간은 등록할 수 없습니다.";
    public final static String NOT_VALID_DUPLICATED_SECTION_STATIONS = "상행역과 하행역이 이미 노선에 모두 등록되어 있어 구간을 등록할 수 없습니다.";
    public final static String NOT_VALID_ANY_STATION = "상행역과 하행역 둘 중 하나라도 포함되어야 구간을 등록할 수 있습니다.";
    public final static String NOT_VALID_UNDER_ZERO_DISTANCE = "거리는 1이상이어야 합니다.";
    public final static String NOT_VALID_EMPTY = "빈 값은 입력할 수 없습니다.";
    public final static String STATION_IS_NOT_NULL = "역 선택은 필수 입니다.";


}
