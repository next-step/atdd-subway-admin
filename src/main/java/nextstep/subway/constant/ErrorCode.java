package nextstep.subway.constant;

public enum ErrorCode {

    지하철역명은_비어있을_수_없음("지하철역명은 비어있을 수 없습니다."),
    노선명은_비어있을_수_없음("노선명은 비어있을 수 없습니다."),
    노선색상은_비어있을_수_없음("노선색상은 비어있을 수 없습니다."),
    상행종착역은_비어있을_수_없음("상행종착역은 비어있을 수 없습니다."),
    하행종착역은_비어있을_수_없음("하행종착역은 비어있을 수 없습니다."),
    ;

    private String errorMessage;

    ErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
