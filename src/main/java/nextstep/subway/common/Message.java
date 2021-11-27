package nextstep.subway.common;

public enum Message {

    SECTION_DISTANCE_REGISTER("등록할 수 없는 구간입니다."),
    BOTH_UP_AND_DOWN("상행, 하행 역 모두가 포함되어있습니다."),
    BOTH_UP_AND_DOWN_NOT("상행, 하행 역 모두가 포함되지 않았습니다.");

    private final String message;

    Message(final String message) { this.message = message; }

    public String getMessage() { return message; }


}
