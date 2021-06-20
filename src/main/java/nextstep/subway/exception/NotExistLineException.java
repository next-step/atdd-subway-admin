package nextstep.subway.exception;

public class NotExistLineException extends RuntimeException {

    private static final String NOT_EXIST_LINE_ERROR_MESSAGE = "존재하지 않는 노선입니다. : ";

    public NotExistLineException(Long id) {
        super(NOT_EXIST_LINE_ERROR_MESSAGE + id);
    }
}
