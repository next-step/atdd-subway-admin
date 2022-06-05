package nextstep.subway.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionRequest {
    private Long upStationId;

    private Long downStationId;

    private Long distance;

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }

    public Section toSection(final Station upStation, final Station downStation) {
        return new Section(upStation, downStation, distance);
    }

}
