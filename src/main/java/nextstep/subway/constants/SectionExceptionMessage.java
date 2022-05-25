package nextstep.subway.constants;

public class SectionExceptionMessage {

    private SectionExceptionMessage() {}

    public static final String UP_STATION_IS_NOT_NULL = "상행역이 할당되어 있지 않습니다.";
    public static final String DOWN_STATION_IS_NOT_NULL = "하행역이 할당되어 있지 않습니다.";
    public static final String DISTANCE_IS_NOT_NULL = "구간이 공란일 수 없습니다.";
    public static final String CANNOT_EQUALS_UP_STATION_WITH_DOWN_STATION = "상행역과 하행역은 같을 수 없습니다.";
    public static final String DISTANCE_IS_MUST_BE_GREATER_THAN_1 = "거리는 1보다 큰 정수로 구성되어야 한다.";
}
