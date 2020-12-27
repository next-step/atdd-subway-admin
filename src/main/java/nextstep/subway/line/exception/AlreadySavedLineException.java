package nextstep.subway.line.exception;

/**
 * @author : leesangbae
 * @project : subway
 * @since : 2020-12-28
 */
public class AlreadySavedLineException extends RuntimeException{

    public AlreadySavedLineException() {
    }

    public AlreadySavedLineException(String message) {
        super(message);
    }
}
