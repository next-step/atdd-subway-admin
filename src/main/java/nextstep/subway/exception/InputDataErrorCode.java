package nextstep.subway.exception;

public enum InputDataErrorCode {
    THERE_IS_NOT_SEARCHED_LINE("[ERROR]검색된 LINE이 없습니다."),
    THERE_IS_A_DUPLICATE_NAME("[ERROR] 중복된 Line 이름이 있습니다."),
    THERE_IS_NOT_SEARCHED_STATION("[ERROR] 검색된 역이 없습니다.");

    private String errorMessage;

    InputDataErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String errorMessage(){
        return this.errorMessage;
    }
}
