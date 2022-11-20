package nextstep.subway.domain;

public enum ErrorMessage {
    LINE_NO_FIND_BY_ID("주어진 id로 지하철호선 정보를 찾을 수 없습니다.");

    private String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
