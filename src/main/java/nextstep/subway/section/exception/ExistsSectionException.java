package nextstep.subway.section.exception;

public class ExistsSectionException extends RuntimeException {
    public ExistsSectionException() {
        super("이미 존재하는 구간입니다");
    }
}
