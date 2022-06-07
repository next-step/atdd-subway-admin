package nextstep.subway.exception;

public class UpStationNotFoundException extends RuntimeException {

    public UpStationNotFoundException() {
        super("상행역이 존재하지 않습니다");
    }

}
