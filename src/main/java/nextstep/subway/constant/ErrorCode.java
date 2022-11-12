package nextstep.subway.constant;

public enum ErrorCode {

    노선_또는_지하철명은_비어있을_수_없음("[ERROR] 노선 또는 지하철역명은 비어있을 수 없습니다."),
    노선색상은_비어있을_수_없음("[ERROR] 노선색상은 비어있을 수 없습니다."),
    상행종착역은_비어있을_수_없음("[ERROR] 상행종착역은 비어있을 수 없습니다."),
    하행종착역은_비어있을_수_없음("[ERROR] 하행종착역은 비어있을 수 없습니다."),
    노선거리는_비어있을_수_없음("[ERROR] 노선거리는 비어있을 수 없습니다."),
    노선거리는_음수일_수_없음("[ERROR] 노선거리는 음수일 수 없습니다."),
    해당하는_노선_없음("[ERROR] 해당하는 노선이 없습니다."),
    ;

    private String errorMessage;

    ErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
