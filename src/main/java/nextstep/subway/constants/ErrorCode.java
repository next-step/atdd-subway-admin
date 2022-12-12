package nextstep.subway.constants;

public enum ErrorCode {
    NO_SUCH_LINE_EXCEPTION("[ERROR] 일치하는 LINE이 없습니다."),
    NO_SUCH_STATION_EXCEPTION("[ERROR] 일치하는 STATION이 없습니다."),
    NO_SUCH_STATION_IN_THE_LINE_EXCEPTION("[ERROR] 삭제대상 LINE에 일치하는 STATION이 없습니다."),
    NO_EMPTY_STATION_NAME_EXCEPTION("[ERROR] 지하철 역이름은 공백이 될 수 없습니다."),
    NO_EMPTY_LINE_COLOR_EXCEPTION("[ERROR] 지하철 LINE 색상은 공백이 될 수 없습니다."),
    NO_EMPTY_LINE_NAME_EXCEPTION("[ERROR] 지하철 LINE 이름은 공백이 될 수 없습니다."),
    NO_SAME_SECTION_EXCEPTION("[ERROR] 같은 Section을 추가할 수 없습니다."),
    NO_MATCH_STATION_EXCEPTION("[ERROR] 일치하는 STATION이 없어 구간(SECTION)을 추가할 수 없습니다."),
    ADD_END_POINT_ISSUE("[ERROR] 종점에 추가할 수 없는 SECTION입니다."),
    BOTH_STATION_ALREADY_EXIST_EXCEPTION("[ERROR] 추가하고자 하는 SECTION의 STATION이 이미 존재합니다."),
    CAN_NOT_DELETE_STATION_CAUSE_SECTIONS_SIZE_EXCEPTION("[ERROR] 삭제하고자 하는 LINE은 구간이 적어 삭제할 수 없습니다.\n" +
            "현재 구간 수: "),
    ADD_SECTION_DISTANCE_EXCEPTION("[ERROR] 기존 구간 사이보다 추가할 구간의 거리가 크거나 같아서 추가할 수 없습니다.");

    private final String errorMessage;

    ErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
