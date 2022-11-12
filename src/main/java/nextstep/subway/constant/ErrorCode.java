package nextstep.subway.constant;

public enum ErrorCode {

    노선_또는_지하철명은_비어있을_수_없음("[ERROR] 노선 또는 지하철역명은 비어있을 수 없습니다."),
    노선색상은_비어있을_수_없음("[ERROR] 노선색상은 비어있을 수 없습니다."),
    상행종착역은_비어있을_수_없음("[ERROR] 상행종착역은 비어있을 수 없습니다."),
    하행종착역은_비어있을_수_없음("[ERROR] 하행종착역은 비어있을 수 없습니다."),
    노선거리는_비어있을_수_없음("[ERROR] 노선거리는 비어있을 수 없습니다."),
    노선거리는_0보다_작거나_같을_수_없음("[ERROR] 노선거리는 0보다 작거나 같을 수 없습니다."),
    해당하는_노선_없음("[ERROR] 해당하는 노선이 없습니다."),
    구간의_상행역과_하행역이_동일할_수_없음("[ERROR] 구간의 상행역과 하행역은 동일할 수 없습니다."),
    노선_정보가_없음("[ERROR] 노선 정보는 비어있을 수 없습니다."),
    이미_존재하는_구간("[ERROR] 이미 존재하는 구간입니다."),
    구간의_상행역과_하행역이_모두_노선에_포함되지_않음("[ERROR] 해당 구간의 상행역과 하행역은 모두 노선에 포함되지 않아 존재할 수 없는 구간입니다."),
    ;

    private String errorMessage;

    ErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
