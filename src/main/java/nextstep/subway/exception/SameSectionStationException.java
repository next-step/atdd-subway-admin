package nextstep.subway.exception;

public class SameSectionStationException extends RuntimeException {
    private static final String SAME_SECTION_STATION_MESSAGE = "상행역과 하행역 모두가 구간에 등록되어 있으면 추가할 수 없습니다.";

    public SameSectionStationException() {
        super(SAME_SECTION_STATION_MESSAGE);
    }
}
