package nextstep.subway.common.exception;

public class ApiErrorMessage {

    private String message;
    private String detailMessage;

    public ApiErrorMessage(String message, String detailMessage) {
        this.message = message;
        this.detailMessage = detailMessage;
    }

    public String getMessage() {
        return message;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

}
