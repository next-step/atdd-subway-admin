package nextstep.subway.message;

public enum LineMessage {
    ERROR_LINE_NAME_SHOULD_BE_NOT_NULL("노선 이름은 필수입니다."),
    ERROR_LINE_DISTANCE_MORE_THAN_ONE("거리는 1이상 이어야 합니다."),
    ERROR_NOT_FOUND_LINE_BY_ID("주어진 ID와 일치하는 지하철 노선을 찾을 수 없습니다");

    private final String message;

    LineMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
