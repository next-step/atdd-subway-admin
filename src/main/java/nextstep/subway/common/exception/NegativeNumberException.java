package nextstep.subway.common.exception;

public class NegativeNumberException extends RuntimeException {
    private static final String NEGATIVE_NUMBER_MESSAGE = "현재 숫자가 음수입니다. : ";
    private static final long serialVersionUID = 3L;

    public NegativeNumberException(int distance) {
        super(NEGATIVE_NUMBER_MESSAGE + distance);
    }
}
