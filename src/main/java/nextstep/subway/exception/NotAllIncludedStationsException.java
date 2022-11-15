package nextstep.subway.exception;

public class NotAllIncludedStationsException extends BadRequestException {
    public NotAllIncludedStationsException() {
        super("상행역/하행역 모두 노선에 추가되어있지 않습니다.");
    }
}
