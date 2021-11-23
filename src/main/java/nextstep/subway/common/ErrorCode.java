package nextstep.subway.common;

public enum ErrorCode {

    SERVER_ERROR(9999),
    DB_ERROR(401);

    private int code ;

    ErrorCode(int code) {
        this.code = code;
    }
}
