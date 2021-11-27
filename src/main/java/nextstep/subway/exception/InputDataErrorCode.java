package nextstep.subway.exception;

public enum InputDataErrorCode {
    THERE_IS_NOT_SEARCHED_LINE("[ERROR]검색된 LINE이 없습니다."),
    THERE_IS_A_DUPLICATE_NAME("[ERROR] 중복된 Line 이름이 있습니다."),
    THERE_IS_NOT_SEARCHED_STATION("[ERROR] 검색된 역이 없습니다."),
    THEY_ARE_NOT_SEARCHED_STATIONS("[ERROR] 상행역과 하행역이 하나도 없습니다."),
    DISTANCE_IS_NOT_LESS_THEN_ZERO("[ERROR]거리는 0 이하가 될수 없습니다."),
    THE_SECTION_ALREADY_EXISTS("[ERROR] 이미 Section이 존재합니다."),
    DISTANCE_OF_THE_OLD_SECTION_IS_LESS_THAN_NEW_SECTION_DISTANCE("[ERROR] 기존 구간의 거리가 새로운 구간 거리보다 작습니다.");

    private String errorMessage;

    InputDataErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String errorMessage() {
        return this.errorMessage;
    }
}
