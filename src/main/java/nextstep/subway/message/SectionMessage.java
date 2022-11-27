package nextstep.subway.message;

public enum SectionMessage {
    ERROR_UP_STATION_SHOULD_BE_NOT_NULL("구간 추가시 상행역은 필수입니다."),
    ERROR_DOWN_STATION_SHOULD_BE_NOT_NULL("구간 추가시 하행역은 필수입니다."),
    ERROR_UP_AND_DOWN_STATION_SHOULD_BE_NOT_EQUAL("상행역과 하행역이 같습니다."),
    ERROR_NEW_SECTION_DISTANCE_MORE_THAN_ORIGIN_SECTION("기존 역 사이 길이보다 작아야합니다."),
    ERROR_UP_AND_DOWN_STATIONS_ARE_ALREADY_ENROLLED("상행역과 하행역이 이미 노선에 등록되어 있습니다."),
    ERROR_UP_AND_DOWN_STATIONS_ARE_NOT_ENROLLED("상행역과 하행역이 둘 중 하나는 노선에 포함이 되어 있어야 합니다."),
    ERROR_NOT_FOUND_UP_TERMINAL_STATION("상행 종점역을 찾을 수 없습니다."),
    ERROR_NOT_FOUND_UP_STATION("주어진 지하철역과 동일한 상행역을 찾을 수 없습니다."),
    ERROR_NOT_FOUND_DOWN_STATION("주어진 지하철역과 동일한 하행역을 찾을 수 없습니다."),
    ERROR_NOT_FOUND_STATION("주어진 지하철역과 동일한 역을 찾을 수 없습니다."),
    ERROR_SECTIONS_MORE_THAN_TWO_SECTIONS("지하철역을 삭제하기 위해선 구간이 최소 2개 이상이여야 합니다."),
    ERROR_EMPTY_SECTIONS("구간이 등록되어 있지 않습니다.");

    private final String message;

    SectionMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
