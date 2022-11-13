package nextstep.subway.exception;

public class NoStationException extends BadRequestException {
    public NoStationException(Long id) {
        super(String.format("존재하지 않는 역입니다. station id = %d", id));
    }
}
