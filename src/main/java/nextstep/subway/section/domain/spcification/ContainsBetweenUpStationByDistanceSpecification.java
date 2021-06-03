package nextstep.subway.section.domain.spcification;

import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import java.util.List;
import java.util.function.Predicate;

public class ContainsBetweenUpStationByDistanceSpecification implements Predicate<List<Section>> {
    private Station upStation;
    private Distance distance;

    public ContainsBetweenUpStationByDistanceSpecification(Station upStation, Distance distance) {
        this.upStation = upStation;
        this.distance = distance;
    }

    @Override
    public boolean test(List<Section> sections) {
        return sections.stream()
                .anyMatch(item -> item.isUpStationBetween(upStation, distance));
    }
}
