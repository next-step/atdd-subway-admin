package nextstep.subway.domain;

public class NotFoundLineException extends RuntimeException {

    public NotFoundLineException(Long id) {
        super(String.format("해당하는 지하철 노선을 찾을 수 없습니다. (id: %d)", id));
    }
}
