package nextstep.subway.common.exception;

public class IllegalSectionRemoveException extends UnsupportedOperationException {
    private static final String ILLEGAL_SECTION_REMOVE_ERROR = "노선에는 최소 %d개의 구간이 있어야 합니다.";

    public IllegalSectionRemoveException(final int minimumSection) {
        super(String.format(ILLEGAL_SECTION_REMOVE_ERROR, minimumSection));
    }
}
