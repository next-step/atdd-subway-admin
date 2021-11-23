package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        Optional<Section> changeSection = sections.stream()
            .filter(it -> it.getNextStation() == section.getNextStation())
            .findFirst();

        changeSection.ifPresent(value -> value.nextStationUpdate(section.getStation()));

        if (!changeSection.isPresent()) {
            addLastSection(section);
        }
        sections.add(section);
    }

    public List<Station> getStations() {
        List<Station> result = new ArrayList<>();
        Optional<Section> nextSection = getLastSection();

        while (nextSection.isPresent()) {
            Section section = nextSection.get();
            result.add(section.getStation());

            nextSection = sections.stream()
                .filter(it -> section.getStation().equals(it.getNextStation()))
                .findFirst();
        }

        Collections.reverse(result);
        return result;
    }

    public List<Section> getSections() {
        return sections;
    }

    private void addLastSection(Section section) {
        Optional<Section> lastSection = getLastSection();
        if (lastSection.isPresent()) {
            lastSection.get().stationUpdate(section.getNextStation());
            return;
        }

        sections.add(Section.lastOf(section));
    }

    private Optional<Section> getLastSection() {
        return sections.stream()
            .filter(it -> it.getNextStation() == null)
            .findFirst();
    }

}
