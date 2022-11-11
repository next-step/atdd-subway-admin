package nextstep.subway.exception;

public class SameStationException extends BadRequestException {
    public SameStationException() {
        super("같은 역으로 노선을 생성할 수 없습니다.");
    }
}
