package nextstep.subway.line.exception;

public class NotFoundLineException extends RuntimeException {

    private static final long serialVersionUID = 2518856302802957665L;

    public NotFoundLineException(long id) {
        super(id + "에 해당하는 노선을 찾을 수 없습니다.");
    }
}
