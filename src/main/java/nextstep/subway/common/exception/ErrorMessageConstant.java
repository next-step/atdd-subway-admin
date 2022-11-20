package nextstep.subway.common.exception;

public final class ErrorMessageConstant {
    public static final String ERROR_MESSAGE_DEFAULT = "오류가 발생하였습니다.";
    public static final String NOT_EXISTS_LINE = "지하철 노선이 존재하지 않습니다.";
    public static final String NOT_EXISTS_STATION = "지하철 역이 존재하지 않습니다.";
    public static final String VALID_LINE_LENGTH_GREATER_THAN_ZERO = "노선의 길이는 0보다 커야합니다.";
    public static final String VALID_GREATER_OR_EQUAL_LENGTH_BETWEEN_STATION = "추가하는 노선의 길이는 기존 역 사이의 길이보다 크거나 같을 수 없습니다.";
    public static final String EXISTS_STATION = "등록하려는 역이 모두 존재합니다.";
    public static final String EXISTS_UP_STATION_AND_DOWN_STATION = "상행성과 하행선 모두 존재하지 않습니다.";
    public static final String LAST_STATION_NOT_DELETE = "마지막 구간은 삭제할 수 없습니다.";
}
