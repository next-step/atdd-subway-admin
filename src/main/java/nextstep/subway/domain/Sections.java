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
    private static final int MIN_SECTION_COUNT = 1;
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
        if (elements.size() > 0) {
            addValidate(section);
        }
        updateSection(section);
        elements.add(section);
    }

    public void removeByStation(Station station) {
        removeByStationValidate(station);

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

    private void addValidate(Section section) {
        validateContainsAllStations(section);
        validateNotContainsStations(section);
    }

    private void validateContainsAllStations(Section newSection) {
        if (allStations().containsAll(newSection.allStations())) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
        }
    }

    private void validateNotContainsStations(Section newSection) {
        Set<Station> stations = allStations();
        if (!stations.contains(newSection.getUpStation()) && !stations.contains(newSection.getDownStation())) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
        }
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

    private Set<Station> allStations() {
        return elements.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toSet());
    }

    private void removeByStationValidate(Station station) {
        validateHasOneSection();
        validateNotContainsStation(station);
    }

    private void validateHasOneSection() {
        if (elements.size() == MIN_SECTION_COUNT) {
            throw new IllegalStateException("단일 구간인 노선은 구간을 제거할 수 없습니다.");
        }
    }

    private void validateNotContainsStation(Station station) {
        if (!allStations().contains(station)) {
            throw new IllegalArgumentException("노선에 등록되지 않은 구간은 제거할 수 없습니다.");
        }
    }

    @Override
    public String toString() {
        return "Sections{" +
                "elements=" + elements +
                '}';
    }
}
