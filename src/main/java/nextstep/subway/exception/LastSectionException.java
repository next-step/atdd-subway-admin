package nextstep.subway.exception;

public class LastSectionException extends BadRequestException {
    public LastSectionException() {
        super("노선의 마지막 구간 역은 지울 수 없습니다.");
    }
}
