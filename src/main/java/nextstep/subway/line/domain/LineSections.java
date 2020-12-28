package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

@Embeddable
public class LineSections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public LineSections() {
    }

    public LineSections(Section upStationSection, Section downStationSection) {
        this.sections = Arrays.asList(upStationSection, downStationSection);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return this.sections.stream()
            .map(Section::getStation)
            .collect(Collectors.toList());
    }

    public void addSection(Section newSection) {
        this.sections.stream()
            .filter(origin -> origin.isPreStationInSection(newSection.getPreStation()))
            .findFirst()
            .ifPresent(origin -> origin.updatePreStationTo(newSection.getStation(), newSection.getDistance()));

        this.sections.add(newSection);
    }

    public List<Section> getOrderedSections() {
        Optional<Section> preLineStation = sections.stream()
            .filter(it -> it.getPreStation() == null)
            .findFirst();

        List<Section> result = new ArrayList<>();
        while (preLineStation.isPresent()) {
            Section preStation = preLineStation.get();
            result.add(preStation);
            preLineStation = sections.stream()
                .filter(section -> section.isPreStationInSection(preStation.getStation()))
                .findFirst();
        }
        return result;
    }
}
