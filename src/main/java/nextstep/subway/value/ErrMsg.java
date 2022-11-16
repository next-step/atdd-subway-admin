package nextstep.subway.value;

public class ErrMsg {
    ErrMsg() {
        throw new AssertionError();
    }
    private static final String CANNOT_FIND_LINE = "번 노선을 찾을 수 없습니다.";

    public static String notFoundLine(Long id) {
        return id+CANNOT_FIND_LINE;
    }
}
