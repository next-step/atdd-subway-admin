package nextstep.subway.section.exception;

public class UnmergeableSectionException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "두 구간은 연결이 불가합니다.";

    public UnmergeableSectionException() {
        super(DEFAULT_MESSAGE);
    }
}
