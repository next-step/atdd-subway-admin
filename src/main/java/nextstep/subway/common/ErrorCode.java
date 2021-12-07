package nextstep.subway.common;

public enum ErrorCode {

    // 노선 [01xx]
    NOT_FOUND_LINE_ID("0101", "존재하지 않는 노선ID 입니다.")
    , NOT_REGISTERED_STATION_TO_LINE("0102", "노선에 등록되지 않은 역입니다.")

    // 역 [02xx]
    , NOT_FOUND_STATION_ID("0201", "존재하지 않는 역ID 입니다.")

    // 구간 [03xx]
    , INVALID_SECTION_DISTANCE("0301", "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.")
    , EXIST_STATIONS("0302", "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.")
    , MUST_CONTAIN_STATION("0303", "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.")
    , CAN_NOT_DELETE_SECTION("0304", "삭제할 수 있는 구간이 없습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
