package nextstep.subway.common;

public enum Message {

    NOT_FIND_LINE("해당 노선은 존재하지 않습니다."),
    NOT_FIND_STATION("해당 역은 존재하지 않습니다."),
    NOT_REGISTER_SECTION_DISTANCE("등록할 수 없는 구간입니다."),
    NOT_REGISTER_ALL_INCLUDE("상행, 하행 역 모두가 포함되어 있어 등록할 수 없습니다."),
    NOT_REGISTER_NOT_ALL_INCLUDE("상행, 하행 역 모두가 포함되지 않아서 등록할 수 없습니다.");

    private final String message;

    Message(final String message) { this.message = message; }

    public String getMessage() { return message; }


}
