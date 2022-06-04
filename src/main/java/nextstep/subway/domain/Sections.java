package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    public static final int MIN_SECTION_COUNT = 1;
    private static final int MIN_STATION_COUNT = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> elements;

    protected Sections() {
        this.elements = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.elements = sections;
    }

    public static Sections empty() {
        return new Sections();
    }

    public void add(Section section) {
        updateSection(section);
        elements.add(section);
    }

    public void removeByStation(Station station) {
        Sections deleteSections = sectionsContainsStation(station);
        if (deleteSections.size() == MIN_STATION_COUNT) {
            removeStartOrEndStation(deleteSections);
            return;
        }
        removeMiddleStation(deleteSections, station);
    }

    public List<Section> get() {
        return elements;
    }

    public int size() {
        return elements.size();
    }

    private void removeStartOrEndStation(Sections sections) {
        elements.removeAll(sections.get());
    }

    private void removeMiddleStation(Sections sections, Station station) {
        Section sectionContainsUpStation = sections.sectionContainsUpStation(station).get();
        Section sectionContainsDownStation = sections.sectionContainsDownStation(station).get();
        Section newSection = new Section(
                sectionContainsDownStation.getUpStation(),
                sectionContainsUpStation.getDownStation(),
                sectionContainsUpStation.getDistance().add(sectionContainsDownStation.getDistance())
        );

        elements.remove(sectionContainsUpStation);
        elements.remove(sectionContainsDownStation);

        add(newSection);
    }

    private Sections sectionsContainsStation(Station station) {
        return new Sections(elements.stream()
                .filter(section -> section.getUpStation().equals(station) || section.getDownStation().equals(station))
                .collect(Collectors.toList())
        );
    }

    private void updateSection(Section newSection) {
        sectionContainsUpStation(newSection.getUpStation())
                .ifPresent(section -> section.updateUpStation(newSection));

        sectionContainsDownStation(newSection.getDownStation())
                .ifPresent(section -> section.updateDownStation(newSection));
    }

    private Optional<Section> sectionContainsUpStation(Station station) {
        return elements.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst();
    }

    private Optional<Section> sectionContainsDownStation(Station station) {
        return elements.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst();
    }

    public Set<Station> allStations() {
        return elements.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "Sections{" +
                "elements=" + elements +
                '}';
    }
}
