package nextstep.subway.exception;

public class DownStationNotFoundException extends RuntimeException {

    public DownStationNotFoundException() {
        super("하행역이 존재하지 않습니다");
    }

}
