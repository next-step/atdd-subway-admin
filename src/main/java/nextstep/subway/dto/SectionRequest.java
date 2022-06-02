package nextstep.subway.dto;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionRequest {
    private final long upStationId;
    private final long downStationId;
    private final int distance;

    public SectionRequest(long upStationId, long downStationId, int distance) {
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

    public Section toSection(Station upStation, Station downStation) {
        return Section.builder(upStation, downStation, Distance.valueOf(this.distance))
                .build();
    }
}
