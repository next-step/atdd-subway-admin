package nextstep.subway.exception;

public class NotIncludeOneStationException extends RuntimeException{
    private static final String NOT_INCLUDE_ONE_STATION_MESSAGE = "상행역과 하행역 둘 중 하나는 구간에 등록 되어 있어야 합니다.";

    public NotIncludeOneStationException(){
        super(NOT_INCLUDE_ONE_STATION_MESSAGE);
    }
}
