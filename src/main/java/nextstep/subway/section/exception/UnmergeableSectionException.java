package nextstep.subway.section.exception;

public class UnmergeableSectionException extends RuntimeException {

    private final static String message = "두 구간은 연결이 불가합니다.";

    public UnmergeableSectionException() {
        super(message);
    }
}
