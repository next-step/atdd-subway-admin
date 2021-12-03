package nextstep.subway.common.exception;

public class SameStationsInSectionException extends RuntimeException {
    public static final String SAME_STATIONS_IN_SECTION_EXCEPTION = "한 구간을 같은 역을 가리킬 수 없습니다. : ";
    private static final long serialVersionUID = 8L;

    public SameStationsInSectionException(String name) {
        super(SAME_STATIONS_IN_SECTION_EXCEPTION + name);
    }
}
