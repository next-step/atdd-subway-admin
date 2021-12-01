package nextstep.subway.line.exception;

public class NotFoundStationInLineException extends RuntimeException {
    public NotFoundStationInLineException(Long stationId) {
        super("구간에 " + stationId + " 지하철역이 없습니다");
    }
}
