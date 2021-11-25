package nextstep.subway.global.error;

public enum ErrorCode {

    INVALID_INPUT_VALUE(400, "C001", " Invalid Input Value"),
    METHOD_NOT_ALLOWED(405, "C002", " Method Not Allow"),
    ENTITY_NOT_FOUND(400, "C003", " Entity Not Found"),
    INTERNAL_SERVER_ERROR(500, "C004", "Server Error"),

    // Section
    ALREADY_REGISTER_SECTION(400, "S001", "상행역과 하행역이 이미 노선에 모두 등록되어 있습니다."),
    STANDARD_STATION_NOT_FOUND(400, "S002", "상행역과 하행역 둘 중 하나가 포함되어야 합니다."),
    DISTANCE_EXCESS(400, "S003", "새로 추가할 역의 길이가 기존 역 사이 길이보다 크거나 같습니다."),
    DISTANCE_UNDER(400, "S004", "역 사이에 거리는 최소 1 이상 입니다.");

    private final String code;
    private final String message;
    private int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }
}
