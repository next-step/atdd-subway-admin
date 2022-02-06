package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionRequest {

    private long downStationId;

    private long upStationId;

    private int distance;

    public SectionRequest(long downStationId, long upStationId, int distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public Section of(Line line, Station upStation, Station downStation) {
        return Section.of(line, upStation, downStation, distance);
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }
}
