package nextstep.subway.line.exception;

public class SingleSectionException extends RuntimeException{

    public SingleSectionException() {
        super("단일구간 노선의 마지막 역은 제거할 수 없습니다.");
    }
}
