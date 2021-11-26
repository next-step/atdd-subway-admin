package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections of() {
        return new Sections();
    }

    public void add(Section section) {
        this.sections
                .stream()
                .filter(s -> s.getUpStation() == section.getUpStation())
                .findFirst()
                .ifPresent(s -> s.updateUpSection(section));

        this.sections
                .stream()
                .filter(s -> s.getDownStation() == section.getDownStation())
                .findFirst()
                .ifPresent(s -> s.updateDownSection(section));

        this.sections.add(section);
    }

    public List<Station> orderedStations() {
        Section firstSection = findFirstSection();
        return createStations(firstSection);
    }

    private List<Station> createStations(Section section) {
        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());
        stations.add(section.getDownStation());

        Optional<Section> optionalSection = findNextSection(section);
        while (optionalSection.isPresent()) {
            Section nextSection = optionalSection.get();
            stations.add(nextSection.getDownStation());
            optionalSection = findNextSection(nextSection);
        }
        return stations;
    }

    private Optional<Section> findNextSection(Section section) {
        return this.sections
                .stream()
                .filter(s -> s.getUpStation() == section.getDownStation())
                .findFirst();
    }

    private Section findFirstSection() {
        List<Station> upStations = new ArrayList<>();
        List<Station> downStations = new ArrayList<>();
        for (Section section : this.sections) {
            upStations.add(section.getUpStation());
            downStations.add(section.getDownStation());
        }
        upStations.removeAll(downStations);
        Station firstStation = upStations.get(0);

        return this.sections
                .stream()
                .filter(s -> s.getUpStation() == firstStation)
                .findFirst().get();
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
