package nextstep.subway.section.domain.spcification;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import java.util.List;
import java.util.function.Predicate;

public class ContainsStationsExactlySpecification implements Predicate<List<Section>>{
    private Station upStation;
    private Station downStation;

    public ContainsStationsExactlySpecification(Station upStation, Station downStation) {
        this.upStation = upStation;
        this.downStation = downStation;
    }

    @Override
    public boolean test(List<Section> sections) {
        return containsStation(sections, upStation) &&
                containsStation(sections, downStation);
    }

    private boolean containsStation(List<Section> sections, Station station) {
        return sections.stream()
                .anyMatch(item -> item.isContains(station));
    }
}
