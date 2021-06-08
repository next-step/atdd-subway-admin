package nextstep.subway.line.ui;

public class CannotDeleteOnlySectionException extends RuntimeException {
    public CannotDeleteOnlySectionException() {
        super("등록된 섹션이 하나뿐이라 역을 삭제할 수 없습니다");
    }
}
