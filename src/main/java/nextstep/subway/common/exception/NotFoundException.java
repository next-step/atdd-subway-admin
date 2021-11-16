package nextstep.subway.common.exception;

public class NotFoundException extends Exception {

    public NotFoundException() {
        super("결과를 찾을 수 없습니다.");
    }

    public NotFoundException(String message) {
        super(message);
    }

}
