package nextstep.subway.wrappers;

import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section otherSection) {
        if (!contains(otherSection)) {
            sections.add(otherSection);
        }
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public List<Station> generateStations() {
        List<Station> responseStations = new LinkedList<>();
        Station startStation = this.calcStartSection();
        responseStations.add(startStation);
        Section section = findSectionByUpStation(startStation);
        while (section != null) {
            Station downStation = section.downStation();
            responseStations.add(downStation);
            section = findSectionByUpStation(downStation);
        }
        return responseStations;
    }

    public void updateSectionByDownStation(LineStation lineStation, Distance distance) {
        sections.stream()
                .filter(section -> section.isEqualDownStation(lineStation.getStation()))
                .findFirst()
                .ifPresent(section -> section.update(
                        section.getLine(),
                        lineStation.getPreStation(),
                        section.downStation(),
                        distance)
                );
    }

    private Station calcStartSection() {
        Station startStation = null;
        for (Section section : sections) {
            startStation = section.calcUpStation(startStation);
        }
        return startStation;
    }

    private Section findSectionByUpStation(Station station) {
        return sections.stream().filter(section -> section.isEqualUpStation(station)).findFirst().orElse(null);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Sections sections1 = (Sections) object;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
