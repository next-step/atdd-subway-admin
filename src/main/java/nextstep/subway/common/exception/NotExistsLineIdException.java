package nextstep.subway.common.exception;

public class NotExistsLineIdException extends NotExistsIdException {
    public NotExistsLineIdException(Long id) {
        super("line_id " + id + " is not exists");
    }
}
