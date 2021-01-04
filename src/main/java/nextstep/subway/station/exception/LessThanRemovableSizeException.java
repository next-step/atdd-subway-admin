package nextstep.subway.station.exception;

/**
 * @author : leesangbae
 * @project : subway
 * @since : 2021-01-04
 */
public class LessThanRemovableSizeException extends RuntimeException {

    public LessThanRemovableSizeException() {
    }

    public LessThanRemovableSizeException(String message) {
        super(message);
    }
}
