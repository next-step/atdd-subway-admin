package nextstep.subway.common.exception;

public class InvalidDuplicatedSection extends RuntimeException {
    public static final String INVALID_DUPLICATED_SECTION = "현재 입력된 역들이 겹치는 구간입니다. %s, %s";
    private static final long serialVersionUID = 5L;
    public InvalidDuplicatedSection(String upStationName, String downStationName) {
        super(String.format(INVALID_DUPLICATED_SECTION, upStationName, downStationName));
    }
}
