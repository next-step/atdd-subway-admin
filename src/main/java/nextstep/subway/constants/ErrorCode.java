package nextstep.subway.constants;

public enum ErrorCode {
    NO_SUCH_LINE_EXCEPTION("[ERROR] 질문을 삭제할 권한이 없습니다.");

    private final String errorMessage;

    ErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
