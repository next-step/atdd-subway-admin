package nextstep.subway.dto;


import nextstep.subway.exception.SubwayException;
import nextstep.subway.exception.SubwayExceptionMessage;

public class SectionRequest {
    private Long upStationId;

    private Long downStationId;

    private int distance;

    protected SectionRequest(final Long upStationId, final Long downStationId, final int distance) {
        validateStations(upStationId, downStationId);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateStations(final Long upStationId, final Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new SubwayException(SubwayExceptionMessage.EQUALS_UP_AND_DOWN_STATION);
        }
    }

    public static SectionRequest of(final Long upStationId, final Long downStationId, final int distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
