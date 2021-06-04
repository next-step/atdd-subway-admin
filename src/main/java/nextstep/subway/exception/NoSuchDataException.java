package nextstep.subway.exception;

public class NoSuchDataException extends DataException {
    public NoSuchDataException(String message, String field, String rejectedValue, String objectName) {
        super(message, field, rejectedValue, objectName);
    }
}
