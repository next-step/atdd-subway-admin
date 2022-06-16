package nextstep.subway.dto;


import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.exception.SubwayExceptionMessage;

public class SectionRequest {
    private long upStationId;

    private long downStationId;

    private int distance;

    protected SectionRequest(final long upStationId, final long downStationId, final int distance) {
        validateStations(upStationId, downStationId);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateStations(final long upStationId, final long downStationId) {
        if (upStationId == downStationId) {
            throw new SubwayException(SubwayExceptionMessage.EQUALS_UP_AND_DOWN_STATION);
        }
    }

    public static SectionRequest of(final long upStationId, final long downStationId, final int distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Section toSection(final Line line, final Station upStation, final Station downStation) {
        return new Section(line, upStation, downStation, distance);
    }
}
