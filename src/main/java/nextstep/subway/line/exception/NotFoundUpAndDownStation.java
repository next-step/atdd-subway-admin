package nextstep.subway.line.exception;

public class NotFoundUpAndDownStation extends RuntimeException {
    public NotFoundUpAndDownStation() {
        super("상행역과 하행역 둘중 하나는 노선에 등록되어 있어야 합니다.");
    }
}
