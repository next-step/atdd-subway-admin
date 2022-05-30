package nextstep.subway.global.exception;

public enum ExceptionType {
    IS_NOT_OVER_ORIGIN_DISTANCE("등록할 구간이 기존 구간보다 길거나 같을 수 없습니다."),
    IS_EXIST_BOTH_STATIONS("두 역이 모두 이미 존재합니다."),
    IS_NOT_EXIST_BOTH_STATIONS("두 역이 모두 존재하지 않습니다."),
    MUST_BE_AT_LEAST_LENGTH_ONE("길이가 최소 1 이상 이어야 합니다."),
    IS_EMPTY_LINE_NAME("지하철 노선명이 없습니다."),
    IS_EMPTY_LINE_COLOR("지하철 노선 색상이 없습니다."),
    IS_EMPTY_LINE_DISTANCE("노선사이의 거리가 없습니다.");

    private final String message;

    ExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
