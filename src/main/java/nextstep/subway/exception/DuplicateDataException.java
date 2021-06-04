package nextstep.subway.exception;

public class DuplicateDataException extends DataException {
    public DuplicateDataException(String message, String field, String rejectedValue, String objectName) {
        super(message, field, rejectedValue, objectName);
    }
}
