package nextstep.subway.section.domain.spcification;

import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import java.util.List;

public class SectionsAddableSpecifications {
    private List<Section> sections;
    private Station upStation;
    private Station downStation;
    private Distance distance;

    public SectionsAddableSpecifications(List<Section> sections, Station upStation, Station downStation, Distance distance) {
        this.sections = sections;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isAddable() {
        return new ContainsBetweenDownStationByDistanceSpecification(downStation, distance).negate()
                .and(new ContainsBetweenUpStationByDistanceSpecification(upStation, distance).negate())
                .and(new ContainsStationsExactlySpecification(upStation, downStation).negate())
                .and(new ContainsStationAnySpecification(upStation, downStation))
                .test(sections);
    }
}
