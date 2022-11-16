package nextstep.subway.exception;

public class CannotRemoveSectionException extends RuntimeException {

    public static final String ONE_SECTION_REMAINS = "구간이 하나인 선에서 구간을 뺄 수 없습니다.";
    public static final String NOT_EXISTS_STATION = "노선에 등록되어 있지 않은 역을 제거할 수 없습니다.";

    public CannotRemoveSectionException(String message) {
        super(message);
    }
}
