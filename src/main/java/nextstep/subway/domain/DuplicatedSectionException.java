package nextstep.subway.domain;

public class DuplicatedSectionException extends RuntimeException {

    private static final long serialVersionUID = 8314932950605424659L;

    public DuplicatedSectionException() {
        super("이미 등록된 구간 입니다.");
    }
}
