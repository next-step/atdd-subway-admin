package nextstep.subway.constant;

public enum ErrorCode {

    지하철역명은_비어있을_수_없음("지하철역명은 비어있을 수 없습니다."),

    ;

    private String errorMessage;

    ErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
