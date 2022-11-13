package nextstep.subway.value;

public class ErrMsg {
    ErrMsg() {
        throw new AssertionError();
    }
    public static final String CANNOT_FIND_LINE = "번 노선을 찾을 수 없습니다.";
}
