package nextstep.subway.exception;

public enum ErrorCode {
    LINE_NULL_POINTER_ERROR("LINE001", "존재하지 않는 노선입니다."),
    STATION_NULL_POINTER_ERROR("STATION01", "존재하지 않는 역입니다."),
    VALID_DISTANCE_ERROR("SECTION02", "상, 하행 종점보다 크거나 같을 수 없습니다."),
    VALID_SAME_STATION_ERROR("SECTION03", "상, 하행 종점역이 같은 구간은 추가할 수 없습니다."),
    VALID_NOT_IN_STATIONS_ERROR("SECTION04", "상, 하행 종점역을 하나라도 포함하지 않는 구간은 추가할 수 없습니다."),
    VALID_DELETE_LAST_STATION_ERROR("SECTION05", "역이 하나인 노선은 삭제할 수 없습니다."),
    VALID_DELETE_NOT_IN_STATIONS_ERROR("SECTION06", "해당 노선에 존재하지 않는 역은 제거할 수 없습니다.");

    private final String errorCode;
    private final String errorMessage;

    ErrorCode(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
