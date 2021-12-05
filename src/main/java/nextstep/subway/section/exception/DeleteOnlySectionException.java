package nextstep.subway.section.exception;

public class DeleteOnlySectionException extends RuntimeException {

    public DeleteOnlySectionException() {
        super("유일한 구간을 삭제할 수 없습니다");
    }
}
