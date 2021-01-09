package nextstep.subway.advice.exception;

public class SectionBadRequestException extends RuntimeException {

    public SectionBadRequestException(Long upStationId, Long downStationId) {
        super(String.format("상행과 하행의 역이 동일할 수 없습니다(상행id: %d, 하행id: %d)", upStationId, downStationId));
    }
}
