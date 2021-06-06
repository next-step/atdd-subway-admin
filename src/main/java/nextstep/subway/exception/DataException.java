package nextstep.subway.exception;

public class DataException extends RuntimeException {
    private String rejectedValue;
    private String field;
    private String objectName;

    public DataException() {}

    public DataException(String message, String field, String rejectedValue, String objectName) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.objectName = objectName;
    }

    public ErrorDto createErrorDto() {
        return new ErrorDto(objectName, field, rejectedValue, getMessage());
    }
}
