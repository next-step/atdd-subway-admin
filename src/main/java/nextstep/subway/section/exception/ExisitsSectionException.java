package nextstep.subway.section.exception;

public class ExisitsSectionException extends RuntimeException {
    public ExisitsSectionException() {
        super("이미 존재하는 구간입니다");
    }
}
