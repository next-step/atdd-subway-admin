package nextstep.subway.exception;

public class NotFoundSection extends RuntimeException {
    public NotFoundSection(Long id) {
        super(String.format("아이디 %d를 포함하는 구간이 존재하지 않습니다.", id));
    }
}
