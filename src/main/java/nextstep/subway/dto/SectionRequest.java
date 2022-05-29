package nextstep.subway.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionRequest implements RequestDTO {
    private long upStationId;         // 상행역 아이디
    private long downStationId;       // 하행역 아이디
    private long distance;             // 거리

    public SectionRequest(long upStationId, long downStationId, long distance) {
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

    public long getDistance() {
        return distance;
    }

    public Section toSection(Station upStation, Station downStation) {
        return new Section(upStation, downStation, distance);
    }
}
