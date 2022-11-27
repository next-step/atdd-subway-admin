package nextstep.subway.domain;

public enum ErrorMessage {
    LINE_NO_FIND_BY_ID("주어진 id로 지하철호선 정보를 찾을 수 없습니다."),
    ALREADY_EXIST_SECTION("상행역과 하행역은 이미 구간에 등록되어 있습니다."),
    NO_EXIST_STATIONS("상행역과 하행역 전부 노선에 존재하지 않습니다."),
    EXCEED_SECTION_DISTANCE("기존에 등록된 구간보다 같거나 큰 길이로는 등록할 수 없습니다."),
    INVALID_DISTANCE_VALUE("구간간의 거리는 자연수여야 합니다."),
    LINE_NOT_CONTAIN_STATION("호선에 존재하지 않는 역입니다."),
    ONLY_ONE_SECTION("호선에 구간이 하나밖에 없어 삭제할 수 없습니다..");

    private String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
