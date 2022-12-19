package nextstep.subway.domain;

public class CannotRemoveSectionException extends RuntimeException {

    private static final long serialVersionUID = -6995141848255148992L;

    public CannotRemoveSectionException() {
        super("지울 수 없는 구간 입니다.");
    }
}
