package nextstep.subway.common;

public class ErrorResponse {
    private int code;
    private String message;

    public ErrorResponse() {
        code = 0;
        message = "데이터 저장중 오류가 발생하였습니다.";
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
