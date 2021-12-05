package nextstep.subway.exception;

public enum InputDataErrorCode {
    THERE_IS_NOT_SEARCHED_LINE("[ERROR]검색된 LINE이 없습니다."),
    THERE_IS_A_DUPLICATE_NAME("[ERROR] 중복된 Line 이름이 있습니다."),
    THERE_IS_NOT_SEARCHED_STATION("[ERROR] 검색된 역이 없습니다."),
    THERE_IS_NOT_SEARCHED_SECTION("[ERROR] 검색된 구간이 없습니다."),
    THEY_ARE_NOT_SEARCHED_STATIONS("[ERROR] 상행역과 하행역이 하나도 없습니다."),
    DISTANCE_IS_NOT_LESS_THEN_ZERO("[ERROR]거리는 0 이하가 될수 없습니다."),
    THE_SECTION_ALREADY_EXISTS("[ERROR] 이미 Section이 존재합니다."),
    THE_STATIONS_ALREADY_EXISTS("[ERROR]이미 지하철역이 등록되어있습니다."),
    THERE_IS_NOT_STATION_IN_LINE("[ERROR]노선에 등록되지 않는 역입니다."),
    THERE_IS_ONLY_ONE_SECTION_IN_LINE("[ERROR]노선안에 구간이 하나 밖에 없습니다.");

    private String errorMessage;

    InputDataErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String errorMessage() {
        return this.errorMessage;
    }
}
