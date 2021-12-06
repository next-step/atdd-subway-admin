package nextstep.subway.section.exception;

public class NotExisitsSectionException extends RuntimeException {
    public NotExisitsSectionException() {
        super("기존의 역과 연결되지 않습니다");
    }
}
