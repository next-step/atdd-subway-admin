package nextstep.subway.exception;

public enum InputDataErrorCode {
    THERE_IS_NOT_SEARCHED_LINE("[ERROR]검색된 LINE이 없습니다."),
    THERE_IS_A_DUPLICATE_NAME("[ERROR] 중복된 Line 이름이 있습니다."),
    THERE_IS_NOT_SEARCHED_STATION("[ERROR] 검색된 역이 없습니다."),
    THEY_ARE_NOT_SEARCHED_STATIONS("[ERROR] 상행역과 하행역이 하나도 없습니다."),
    DISTANCE_IS_NOT_LESS_THEN_ZERO("[ERROR]거리는 0 이하가 될수 없습니다."),
    THE_SECTION_ALREADY_EXISTS("[ERROR] 이미 Section이 존재합니다."),
    THE_STATIONS_ALREADY_EXISTS("[ERROR]이미 지하철역이 등록되어있습니다."),
    DISTANCE_OF_OLD_SECTION_AND_NEW_SECTIONIS_IS_SAME("[ERROR] 기존 구간과 추가하려는 구간 거리가 같습니다."),
    DISTANCE_OF_THE_NEW_SECTION_IS_OVER_THAN_OLD_SECTION_DISTANCE("[ERROR] 기존 구간보다 추가하려는 구간 거리가 큽니다.");


    private String errorMessage;

    InputDataErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String errorMessage() {
        return this.errorMessage;
    }
}
