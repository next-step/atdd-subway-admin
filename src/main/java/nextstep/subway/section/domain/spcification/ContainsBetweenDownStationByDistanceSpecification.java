package nextstep.subway.section.domain.spcification;

import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import java.util.List;
import java.util.function.Predicate;

public class ContainsBetweenDownStationByDistanceSpecification implements Predicate<List<Section>> {
    private Station downStation;
    private Distance distance;

    public ContainsBetweenDownStationByDistanceSpecification(Station downStation, Distance distance) {
        this.downStation = downStation;
        this.distance = distance;
    }

    @Override
    public boolean test(List<Section> sections) {
        return sections.stream()
                .anyMatch(item -> item.isDownStationBetween(downStation, distance));
    }
}
