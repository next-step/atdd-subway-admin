package nextstep.subway.exception;

public class ValueFormatException extends RuntimeException {
    private String rejectedValue;
    private String field;
    private String objectName;

    public ValueFormatException() {}

    public ValueFormatException(String message, String field, String rejectedValue, String objectName) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.objectName = objectName;
    }

    public ErrorDto createErrorDto() {
        return new ErrorDto(objectName, field, rejectedValue, getMessage());
    }
}
