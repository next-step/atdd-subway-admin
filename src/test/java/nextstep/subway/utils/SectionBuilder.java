package nextstep.subway.utils;

import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;

public class SectionBuilder {

    private Station upStation;
    private Station downStation;
    private int distance;

    public SectionBuilder withUpStation(Station station) {
        this.upStation = station;
        return this;
    }

    public SectionBuilder withDownStation(Station station) {
        this.downStation = station;
        return this;
    }

    public SectionBuilder withDistance(int distance) {
        this.distance = distance;
        return this;
    }

    public Section build() {
        return new Section(upStation, downStation, distance);
    }

    public SectionRequest toSectionRequest() {
        return new SectionRequest(upStation.getId(),
                downStation.getId(),
                distance);
    }
}
