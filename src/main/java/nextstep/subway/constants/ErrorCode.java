package nextstep.subway.constants;

public enum ErrorCode {
    NO_SUCH_LINE_EXCEPTION("[ERROR] 질문을 삭제할 권한이 없습니다."),
    NO_EMPTY_STATION_NAME_EXCEPTION("[ERROR] 지하철 역이름은 공백이 될 수 없습니다."),
    NO_EMPTY_LINE_COLOR_EXCEPTION("[ERROR] 지하철 LINE 색상은 공백이 될 수 없습니다."),
    NO_EMPTY_LINE_NAME_EXCEPTION("[ERROR] 지하철 LINE 이름은 공백이 될 수 없습니다."),
    NO_SAME_SECTION_EXCEPTION("[ERROR] 같은 Section을 추가할 수 없습니다."),
    ADD_SECTION_DISTANCE_EXCEPTION("[ERROR] 기존 구간 사이보다 추가할 구간의 거리가 크거나 같아서 추가할 수 없습니다.");

    private final String errorMessage;

    ErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
