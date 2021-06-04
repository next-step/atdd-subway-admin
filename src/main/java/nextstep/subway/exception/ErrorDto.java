package nextstep.subway.exception;

public class ErrorDto {
    private String objectName;
    private String field;
    private String rejectedValue;
    private String message;

    public ErrorDto(String objectName, String field, String rejectedValue, String message) {
        this.objectName = objectName;
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.message = message;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getField() {
        return field;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }

    public String getMessage() {
        return message;
    }
}
