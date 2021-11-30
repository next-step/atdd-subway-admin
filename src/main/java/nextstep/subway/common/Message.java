package nextstep.subway.common;

public class Message {
    public static final String MESSAGE_SECTION_DISTANCE_NOT_LESS_THAN_ZERO = "구간의 거리가 0과 같거나 작을 수 없습니다. 입력된 값[%d]";
    public static final String MESSAGE_IS_NOT_EXISTS_STATION = "기존 노선에 해당역이 존재하지 않습니다. 상행역[%s] 하행역[%s]";

    public static String format(String message, Object ... arg) {
        return String.format(message, arg);
    }
}
