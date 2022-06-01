package nextstep.subway.common.exception;

public enum ErrorMessage {
    ALREADY_REGISTERED_ERROR("상행 역과 하행 역이 이미 모두 등록되어 있어 구간 추가할 수 없습니다."),
    NO_EXISTS_STATION_ADD_ERROR("상행 역과 하행 역 둘 중 하나라도 등록되어 있지 않으면 구간 추가할 수 없습니다."),
    SECTIONS_SIZE_ERROR("노선의 구간이 1개인 경우, 지하철 역을 제거할 수 없습니다."),
    NO_EXISTS_STATION_REMOVE_ERROR("노선에 등록되어 있지 않은 역은 제거할 수 없습니다."),
    OVER_SIZED_DISTANCE_ERROR("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다."),
    NO_EXISTS_STATION_ERROR("해당 지하철 역이 존재하지 않습니다."),
    NO_EXISTS_LINE_ERROR("해당 노선은 존재하지 않습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
