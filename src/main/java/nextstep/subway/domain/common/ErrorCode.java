package nextstep.subway.domain.common;

public enum ErrorCode {

    DATA_INTEGRITY_VIOLATION("데이터 정합성에 문제가 발생했습니다.");

    final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
