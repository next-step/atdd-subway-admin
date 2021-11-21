package nextstep.subway.common.exception;

public class NoResultException extends RuntimeException {

    public NoResultException() {
        super("결과가 없습니다.");
    }

    public NoResultException(String message) {
        super(message);
    }

}
