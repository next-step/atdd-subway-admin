package nextstep.subway.section.exception;

public class OnlyOneSectionExistException extends RuntimeException {

    public OnlyOneSectionExistException() {
        super("해당 노선의 구간이 1개만 남아 있어서 해당역을 삭제하실 수 없습니다.");
    }
}
