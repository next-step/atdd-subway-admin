package nextstep.subway.line.exception;

public class AlreadyRegisteredSectionException extends RuntimeException {
    public AlreadyRegisteredSectionException() {
        super("이미 등록된 구간입니다.");
    }
}
