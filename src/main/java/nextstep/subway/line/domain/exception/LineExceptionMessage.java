package nextstep.subway.line.domain.exception;

public enum LineExceptionMessage {

    LINE_NAME_IS_NOT_NULL("지하철노선의 명은 공란일 수 없습니다."),
    LINE_COLOR_IS_NOT_NULL("지하철노선의 노선 색상은 공란일 수 없습니다."),
    ALREADY_ADDED_SECTION("이미 등록된 구간은 다시 등록될 수 없습니다.");

    String message;

    LineExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
