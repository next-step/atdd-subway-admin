package nextstep.subway.advice.exception;

public class SectionBadRequestException extends RuntimeException {

    public SectionBadRequestException(Long upStationId, Long downStationId) {
        super(String.format("상행과 하행의 역이 동일할 수 없습니다(상행id: %d, 하행id: %d)", upStationId, downStationId));
    }

    public SectionBadRequestException(int originDistance, int newDistance) {
        super(String.format("추가하려는 거리가 기존의 거리보다 더 깁니다 (기존거리: %d , 추가거리: %d)", originDistance, newDistance));
    }
}
