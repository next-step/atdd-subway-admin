package nextstep.subway.section.domain;

import nextstep.subway.section.exception.NotUnderSectionDistanceException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Station> getStations() {
        return this.sections.stream()
                .flatMap(section -> section.getStations().stream())
                .distinct()
                .collect(Collectors.toList());

    }

    public void validateSection(Section newSection) {
        boolean match = sections.stream()
                .anyMatch(section -> section.hasSameUpStation(newSection) && section.getDistance() == newSection.getDistance());

        if (match) {
            throw new NotUnderSectionDistanceException();
        }
    }
}
