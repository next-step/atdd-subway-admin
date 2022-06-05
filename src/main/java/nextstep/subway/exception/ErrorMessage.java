package nextstep.subway.exception;

public enum ErrorMessage {
    NOT_FOUND_STATION("해당 역을 찾을 수 없습니다."),
    NOT_FOUND_LINE("해당 노선을 찾을 수 없습니다."),
    NOT_FOUND_STATION_FOR_ADD_SECTION("구간 등록을 하기 위한 역을 찾을 수 없습니다."),
    ALREADY_REGISTERED_LINE("이미 등록된 노선입니다."),
    NOT_VALID_DISTANCE_FOR_SECTION("기존 역 사이 길이보다 크거나 같으면 새로운 구간을 등록할 수 없습니다."),
    NOT_FOUND_STATIONS_FOR_SECTION("상행역과 하행역 모두 찾을 수 없습니다."),
    NOT_DELETABLE_SIZE_SECTION("구간이 하나인 노선의 마지막 구간은 삭제할 수 없습니다."),
    NOT_FOUND_STATION_FOR_DELETE_SECTION("구간 삭제를 위한 역을 찾을 수 없습니다.");

    private final String message;

    private ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
