package nextstep.subway.domain;

public enum ErrorMessage {
    LINE_NO_FIND_BY_ID("주어진 id로 지하철호선 정보를 찾을 수 없습니다."),
    ALREADY_EXIST_SECTION("상행역과 하행역은 이미 구간에 등록되어 있습니다."),
    NO_EXIST_STATIONS("상행역과 하행역 전부 노선에 존재하지 않습니다.");

    private String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
