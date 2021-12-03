package nextstep.subway.common;

public enum Message {
    MESSAGE_SECTION_DISTANCE_NOT_LESS_THAN_ZERO("구간의 거리가 0과 같거나 작을 수 없습니다. 입력된 값[%d]"),
    MESSAGE_IS_NOT_EXISTS_STATION("기존 노선에 해당역이 존재하지 않습니다. 상행역[%s] 하행역[%s]"),
    MESSAGE_ALREADY_REGISTERED_SECTION("이미 등록되어 있는 구간입니다."),
    MESSAGE_LIMIT_SECTION_SIZE("제거할 수 없습니다. \n 해당 라인의 기점역과 종점역입니다."),
    STATION_DUPLICATION("상행선과 하행선은 동일할 수 없습니다."),
    SECTION_NOT_FOUND_EXCEPTION("해당_구간_영역이_존재하지 않습니다."),
    STATION_NOTFOUND_EXCEPTION ("지하철역이 존재하지 않습니다.");

    private String message;

    Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static String format(Message message, Object... arg) {
        return String.format(message.getMessage(), arg);
    }
}
