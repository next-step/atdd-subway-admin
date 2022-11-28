package nextstep.subway.exception;

public class AllRegisteredStationsException extends BadRequestException {
    public AllRegisteredStationsException() {
        super("이미 상행역/하행역 모두 노선에 추가되어 있습니다.");
    }
}
