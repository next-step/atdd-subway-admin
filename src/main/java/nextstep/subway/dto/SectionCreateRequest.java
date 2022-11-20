package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionCreateRequest {
    private long upStationId;
    private long downStationId;
    private int distance;

    public SectionCreateRequest() {
    }

    public SectionCreateRequest(long upStationId, long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public Section toSection(Station upStation, Station downStation, Line line) {
        return new Section(upStation, downStation, distance, line);
    }
}
