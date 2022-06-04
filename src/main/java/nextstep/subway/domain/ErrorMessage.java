package nextstep.subway.domain;

public enum ErrorMessage {
    LINE_NAME_EMPTY("지하철 노선의 이름은 필수입니다."),
    LINE_COLOR_EMPTY("지하철 노선의 색깔은 필수입니다."),
    UP_STATION_DOWN_STATION_SAME("상행종점역과 하행종점역은 같을 수 없습니다."),
    DISTANCE_UNDER_ZERO("거리는 0 이상 이어야 합니다."),
    ALREADY_CREATED_SECTION("이미 등록된 역을 구간으로 등록할 수 없습니다."),
    NOT_EXISTED_STATION("상행역, 하행역 둘 중 하나를 포함해야 구간으로 등록 할 수 있습니다.");
    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
