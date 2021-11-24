package nextstep.subway.exception;

public enum InputDataErrorCode {
    THERE_IS_NOT_SEARCHED_LINE("[ERROR]검색된 LINE이 없습니다."),
    THERE_IS_A_DUPLICATE_NAME("[ERROR] 중복된 Line 이름이 있습니다."),
    THERE_IS_NOT_SEARCHED_STATION("[ERROR] 검색된 역이 없습니다."),
    THEY_ARE_NOT_SEARCHED_STATIONS("[ERROR] 상행역과 하행역이 하나도 없습니다."),
    ONE_OF_THE_TWO_STATIONS_IS_NOT_REGISTERD_ON_LINE ("[ERROR] 둘 중에 하나가 노선 중에 등록된 역이 없습니다."),
    DISTANCE_IS_NOT_LESS_THEN_ZERO("[ERROR]거리는 0 이하가 될수 없습니다."),
    THE_SECTION_ALREADY_EXISTS("[ERROR] 이미 Section이 존재합니다.");

    private String errorMessage;

    InputDataErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String errorMessage() {
        return this.errorMessage;
    }
}
