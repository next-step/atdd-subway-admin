package nextstep.subway.domain;

public class InvalidSectionDistanceException extends RuntimeException {

    private static final long serialVersionUID = 7304443620255230690L;

    public InvalidSectionDistanceException() {
        super("역과 역 사이의 거리보다 좁은 거리를 입력해주세요.");
    }

}
