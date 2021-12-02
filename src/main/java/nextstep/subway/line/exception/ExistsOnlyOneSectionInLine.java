package nextstep.subway.line.exception;

public class ExistsOnlyOneSectionInLine extends RuntimeException {
    public ExistsOnlyOneSectionInLine() {
        super("하나의 구간만 있을시 삭제할 수 없습니다.");
    }
}
