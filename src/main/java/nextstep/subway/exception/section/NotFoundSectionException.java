package nextstep.subway.exception.section;

public class NotFoundSectionException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String NO_STATION = "존재하지 구간입니다.";

    public NotFoundSectionException() {
        super(NO_STATION);
    }

    public NotFoundSectionException(String message) {
        super(message);
    }
}
